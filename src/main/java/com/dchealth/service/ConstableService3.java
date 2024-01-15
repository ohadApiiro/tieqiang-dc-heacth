package com.dchealth.service;

import com.dchealth.Consts;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.springframework.stereotype.Service;

@Produces("application/json")
@Path(Consts.CONSTLY_PATH)
@Service
public class ConstableService3 {

    @GET
    public Date get() {
        return new Date();
    }
}
