package com.dchealth.service.rare;

import com.dchealth.VO.*;
import com.dchealth.entity.common.YunDictitem;
import com.dchealth.entity.common.YunUsers;
import com.dchealth.entity.rare.*;
import com.dchealth.facade.common.BaseFacade;
import com.dchealth.util.JSONUtil;
import com.dchealth.util.StringUtils;
import com.dchealth.util.UserUtils;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import com.google.cloud.storage;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.*;

public class Googleervice {

    public static Response google_test_method(){
        Storage storage;
        storage.getStorage();
        storage.update();
        return Response.status(Response.Status.OK);
    }

}
