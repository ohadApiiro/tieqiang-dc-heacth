package com.dchealth.service;

import com.dchealth.service.ConstableService1.Consts;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.springframework.stereotype.Service;

@Produces("application/json")
@Path(Consts.PATH)
@Service
public class ConstableService2 {

    @GET
    public Date get(){
        return new Date();
    }
}
