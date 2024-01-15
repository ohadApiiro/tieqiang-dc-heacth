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
import com.amazonaws.services.s3.*;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.*;

public class ExampleCostsClass {

    public static final String PATH1 = "/path1";
    public static final String PATH2 = "/path1";

}
