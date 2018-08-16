package net.weibeld.spring.urlshortener.controller;

import net.weibeld.spring.urlshortener.controller.dto.ShortenUrlRequest;
import net.weibeld.spring.urlshortener.service.IUrlStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.common.hash.Hashing;

@Controller
public class UrlController {

    private Logger logger = LoggerFactory.getLogger(UrlController.class);

    @Autowired
    private IUrlStoreService urlStoreService;

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String showForm(ShortenUrlRequest request) {
        logger.info("Receiving GET /");
        return "shortener";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void redirectToUrl(@PathVariable String id, HttpServletResponse response) throws IOException {
        logger.info("Receiving GET /" + id);
        final String url = urlStoreService.findUrlById(id);
        if (url != null) {
            response.addHeader("Location", url);
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @RequestMapping(value="/", method = RequestMethod.POST)
    public ModelAndView shortenUrl(HttpServletRequest httpRequest, @Valid ShortenUrlRequest urlRequest, BindingResult bindingResult) {
        logger.info("Receiving POST / with data " + urlRequest.getUrl());
        String url = urlRequest.getUrl();
        if (!isUrlValid(url)) {
            bindingResult.addError(new ObjectError("url", "Invalid url format: " + url));
        }
        ModelAndView modelAndView = new ModelAndView("shortener");
        if (!bindingResult.hasErrors()) {
            final String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
            urlStoreService.storeUrl(id, url);
            String requestUrl = httpRequest.getRequestURL().toString();
            String prefix = requestUrl.substring(0, requestUrl.indexOf(httpRequest.getRequestURI(), "http://".length()));

            modelAndView.addObject("shortenedUrl", prefix + "/" + id);
            logger.info("shortenedUrl model object: " + prefix + "/" + id);
        }
        return modelAndView;
    }

    private boolean isUrlValid(String url) {
        boolean valid = true;
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            valid = false;
        }
        return valid;
    }
}
