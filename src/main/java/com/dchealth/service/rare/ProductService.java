package com.dchealth.service.rare;

import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Created by Lagi on 2020/12/13.
 */
@Controller
@Produces("application/json")
@Path("product")
public class ProductService {

    @GET
    @Path("{productId}/queryResource")
    public String queryResource(@QueryParam("productId") String productId) {
        return "1234";
    }

}
