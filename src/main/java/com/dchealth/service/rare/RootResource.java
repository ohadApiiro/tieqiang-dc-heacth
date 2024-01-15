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


@Controller
@Path("root")
public class RootResource {

    @Autowired
    private BaseFacade baseFacade ;


    @Path("sr-locator/")
    public SubResource subresourceLocator(@QueryParam("param") String param) throws Exception {
        return new SubResource();
    }

}



@Controller
@Path("root2")
public class RootResource2 {

    @Autowired
    private BaseFacade baseFacade ;


    @Path("sr-locator2/")
    public SubResource subresourceLocator(@QueryParam("param") String param) throws Exception {
        return new SubResource();
    }

}



@Controller
@Path("root3")
public class RootResource3 {

    @Autowired
    private BaseFacade baseFacade ;


    @Path("sr-locator3/")
    public SubResourceWithoutMethods subresourceLocator(@QueryParam("param") String param) throws Exception {
        return new SubResourceWithoutMethods();
    }

}
