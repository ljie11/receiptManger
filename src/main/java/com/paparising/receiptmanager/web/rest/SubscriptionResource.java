package com.paparising.receiptmanager.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.paparising.receiptmanager.domain.Subscription;
import com.paparising.receiptmanager.service.SubscriptionService;
import com.paparising.receiptmanager.service.dto.SubscriptionResponseDTO;
import com.paparising.receiptmanager.web.rest.util.HeaderUtil;
import com.paparising.receiptmanager.web.rest.util.PaginationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing App direct Subscription.
 */
@RestController
@RequestMapping("/api")
public class SubscriptionResource {

    private final Logger log = LoggerFactory.getLogger(SubscriptionResource.class);
        
    @Inject
    private SubscriptionService subscriptionService;

    /**
     * POST  /subscriptions : Create a new subscription.
     *
     * @param receipt the receipt to create
     * @return the ResponseEntity with status 201 (Created) and with body the new receipt, or with status 400 (Bad Request) if the receipt has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/subscriptions",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SubscriptionResponseDTO> createSubscription(@RequestBody Subscription subscription) throws URISyntaxException {
        log.debug("REST request to save subscription : {}", subscription);
        if (subscription.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("subscription", "idexists", "A new subscription cannot already have an ID")).body(null);
        }
        Subscription result = subscriptionService.save(subscription);
        return null;
    }

    /**
     * PUT  /subscriptions : Updates an existing subscription.
     *
     * @param subscription the subscription to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated subscription,
     * or with status 400 (Bad Request) if the subscription is not valid,
     * or with status 500 (Internal Server Error) if the subscription couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/subscriptions",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SubscriptionResponseDTO> updatesubscription(@RequestBody Subscription subscription) throws URISyntaxException {
        log.debug("REST request to update subscription : {}", subscription);
        if (subscription.getId() == null) {
            return createSubscription(subscription);
        }
        SubscriptionResponseDTO result = new SubscriptionResponseDTO();
        return ResponseEntity.ok()
            .body(result);
    }

    /**
     * GET  /subscriptions : get all the subscriptions.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of subscriptions in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/subscriptions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Subscription>> getAllSubscriptions(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of subscriptions");
        Page<Subscription> page = subscriptionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/subscriptions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /subscriptions/:id : get the "id" subscription.
     *
     * @param id the id of the subscription to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the subscription, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/subscriptions/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Subscription> getSubscription(@PathVariable Long id) {
        log.debug("REST request to get subscription : {}", id);
        Subscription subscription = subscriptionService.findOne(id);
        return Optional.ofNullable(subscription)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /subscriptions/:id : delete the "id" subscription.
     *
     * @param id the id of the subscription to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/subscriptions/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> cancelSubscription(@PathVariable Long id) {
        log.debug("REST request to delete Subscription : {}", id);
        subscriptionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("Subscription", id.toString())).build();
    }

}