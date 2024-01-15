package com.dchealth.service.rare;

import com.dchealth.VO.*;
import com.dchealth.entity.common.YunDictitem;
import com.dchealth.entity.common.YunUsers;
import com.dchealth.entity.rare.*;
import com.dchealth.facade.common.BaseFacade;
import com.dchealth.util.JSONUtil;
import com.dchealth.util.StringUtils;
import com.dchealth.util.UserUtils;
import com.amazonaws.services.s3;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.*;

import ExampleCostsClass;


public class SubResource {

    
    @GET
    @Path("get")
    public String subresourceMethodGet(@QueryParam("param21") String param21) throws Exception {
        return "Get";
    }

    @POST
    @Path("post")
    public String subresourceMethodPost(@QueryParam("param22") String param22) throws Exception {
        return "Post";
    }


}


public class SubResourceWithoutMethods {

    
    public String someFunc() throws Exception {
        return "Get";
    }



}
