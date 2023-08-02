package com.example.springcashier;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.Optional;
import java.time.*;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64.Encoder;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("admin/cashier")
public class SpringCashierController {

    @Value("${spring.cashier.apikey}") String API_KEY ;
    @Value("${spring.cashier.apihost}") String API_HOST ;

    @Autowired private StarbucksDrinkRepository drinksRepository;

    @Autowired
    private RabbitTemplate rabbit;

    @Autowired
    private Queue queue;

    @GetMapping
    public String getAction( @ModelAttribute("command") Command command, 
                            Model model, HttpSession session) {

        String message = "" ;

        command.setRegister( "5012349" ) ;
        message = "Starbucks Reserved Order" + "\n\n" +
            "Register: " + command.getRegister() + "\n" +
            "Status:   " + "Ready for New Order"+ "\n" ;

        String server_ip = "" ;
        String host_name = "" ;
        try { 
            InetAddress ip = InetAddress.getLocalHost() ;
            server_ip = ip.getHostAddress() ;
            host_name = ip.getHostName() ;
        } catch (Exception e) { }

        model.addAttribute( "message", message ) ;
        model.addAttribute( "server",  host_name + "/" + server_ip ) ;

        return "admin/starbucks" ;

    }

    @PostMapping
    public String postAction(@Valid @ModelAttribute("command") Command command,  
                            @RequestParam(value="action", required=true) String action,
                            Errors errors, Model model, HttpServletRequest request) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String resourceUrl = "" ;
        String message = "" ;

        // Set API Key Header
        headers.set( "apikey", API_KEY ) ;

        log.info( "Action: " + action ) ;
        command.setRegister( command.getStores() ) ;
        log.info( "Command: " + command ) ;

        /* Process Post Action */
        if ( action.equals("Place Order") ) {
            resourceUrl = "http://"+API_HOST+"/order/register/"+command.getRegister();

            try {
                DrinkOrder orderRequest = DrinkOrder.GetNewOrder();
                orderRequest.setRegister(command.getRegister());

                HttpEntity<DrinkOrder> newOrderRequest = new HttpEntity<DrinkOrder>(orderRequest, headers);
                ResponseEntity<DrinkOrder> newOrderResponse = restTemplate.postForEntity(resourceUrl, newOrderRequest, DrinkOrder.class);

                DrinkOrder newOrder = newOrderResponse.getBody();
                System.out.println(newOrder);

                //save order to processor repo
                StarbucksDrink drinkRequest = new StarbucksDrink();
                drinkRequest.setDrink(newOrder.getDrink());
                drinkRequest.setMilk(newOrder.getMilk());
                drinkRequest.setSize(newOrder.getSize());
                drinkRequest.generateDrinkID();
                drinksRepository.save(drinkRequest);

                System.out.println(drinkRequest.getDrinkid());
                rabbit.convertAndSend(queue.getName(), drinkRequest.getDrinkid());

                message = "Starbucks Reserved Order" + "\n\n" +
                        "Drink: " + newOrder.getDrink() + "\n" +
                        "Milk:  " + newOrder.getMilk() + "\n" +
                        "Size:  " + newOrder.getSize() + "\n" +
                        "Total: " + newOrder.getTotal() + "\n" +
                        "\n" +
                        "Register: " + newOrder.getRegister() + "\n" +
                        "Status:   " + newOrder.getStatus() + "\n";

            } catch (Exception e) {
                message = "Starbucks Reserved Order" + "\n\n" +
                        "This register already has an order. Please clear the order " +
                        "before placing another order at this register \n\n" +
                        "Register: " + command.getRegister() + "\n" +
                        "Status:   " + "Ready for Payment"+ "\n" ;
            }

        }
        else if ( action.equals("Get Order") ) {
//            resourceUrl = "http://localhost:8080/order/register/" + command.getRegister();  //for local testing
            resourceUrl = "http://"+API_HOST+"/order/register/"+command.getRegister();      //for testing via docker/kong




            // pretty print JSON
            try {
                HttpEntity<String> getOrderRequest = new HttpEntity<String>(headers);
                ResponseEntity<DrinkOrder> getOrderResponse = restTemplate.exchange(resourceUrl,HttpMethod.GET, getOrderRequest, DrinkOrder.class);

                DrinkOrder getOrder = getOrderResponse.getBody();
                System.out.println(getOrder);

                message = "Starbucks Reserved Order" + "\n\n" +
                        "Drink: " + getOrder.getDrink() + "\n" +
                        "Milk:  " + getOrder.getMilk() + "\n" +
                        "Size:  " + getOrder.getSize() + "\n" +
                        "Total: " + getOrder.getTotal() + "\n" +
                        "\n" +
                        "Register: " + getOrder.getRegister() + "\n" +
                        "Status:   " + getOrder.getStatus() + "\n";
            } catch (Exception e) {
                message = "Starbucks Reserved Order" + "\n\n" +
                        "No order active at this register \n\n" +
                        "Register: " + command.getRegister() + "\n" +
                        "Status:   " + "Ready for New Order"+ "\n" ;
            }

        } 
        else if ( action.equals("Clear Order") ) {
            resourceUrl = "http://"+API_HOST+"/order/register/"+command.getRegister();      //for testing via docker/kong

            try {
                HttpEntity<DrinkOrder> deleteRequest = new HttpEntity<DrinkOrder>(headers);
                ResponseEntity<DrinkOrder> deleteResponse = restTemplate.exchange(resourceUrl,HttpMethod.DELETE, deleteRequest, DrinkOrder.class);

                message = "Starbucks Reserved Order" + "\n\n" +
                        "Order removed\n\n" +
                        "Register: " + command.getRegister() + "\n" +
                        "Status:   " + "Ready for New Order"+ "\n" ;

            } catch (Exception e) {
                message = "Starbucks Reserved Order" + "\n\n" +
                        "No order active at this register\n\n" +
                        "Register: " + command.getRegister() + "\n" +
                        "Status:   " + "Ready for New Order"+ "\n" ;
            }

        }         
        command.setMessage( message ) ;

        String server_ip = "" ;
        String host_name = "" ;
        try { 
            InetAddress ip = InetAddress.getLocalHost() ;
            server_ip = ip.getHostAddress() ;
            host_name = ip.getHostName() ;
        } catch (Exception e) { }

        model.addAttribute( "message", message ) ;
        model.addAttribute( "server",  host_name + "/" + server_ip ) ;

        return "admin/starbucks" ;

    }
    

}

