package com.dchealth.service.rare;

import com.dchealth.VO.Page;
import com.dchealth.entity.rare.YunBriefing;
import com.dchealth.facade.common.BaseFacade;
import com.dchealth.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sunkqa on 2018/2/27.
 */
@Controller
@Produces("application/json")
@Path("briefing")
public class BriefingService {

    @Autowired
    private BaseFacade baseFacade;

    /**
     *根据标题获取简报信息
     * @param title 简报-标题
     * @return
     */
    @GET
    @Path("list-briefing-data")
    public List<YunBriefing> getBriefingDataList(@QueryParam("title") String title){
        String hql = " from YunBriefing where status<>'-1' ";
        if(title!=null && !"".equals(title)){
            hql += " and title = '"+title+"'";
        }

        hql +=" order by createDate desc";
        return baseFacade.createQuery(YunBriefing.class,hql,new ArrayList<Object>()).getResultList();
    }

    @GET
    @Path("get-briefing-page")
    public Page<YunBriefing> getBriefingPageResult(@QueryParam("title") String title,@QueryParam("perPage")int perPage,@QueryParam("currentPage")int currentPage){
        String hql = " from YunBriefing where status<>'-1' ";
        if(title!=null && !"".equals(title)){
            hql += " and title = '"+title+"'";
        }

        hql +=" order by createDate desc";
        return baseFacade.getPageResult(YunBriefing.class,hql,perPage,currentPage);
    }

    /**
     * 根据id查询简报信息
     * @param id 简报id
     * @return
     * @throws Exception
     */
    @GET
    @Path("find-briefing")
    public YunBriefing getYunBriefing(@QueryParam("id") String id) throws Exception{
        return baseFacade.get(YunBriefing.class,id);
    }

    /**
     * 添加或修改,删除简报信息
     * @param yunBriefing
     * @return
     */
    @POST
    @Path("merge")
    @Transactional
    public Response mergeYunBriefing(YunBriefing yunBriefing){
        if(StringUtils.isEmpty(yunBriefing.getId())){
            yunBriefing.setStatus("1");
        }
        yunBriefing.setModifyDate(new Timestamp(new Date().getTime()));
        YunBriefing merge = baseFacade.merge(yunBriefing);
        return Response.status(Response.Status.OK).entity(merge).build();
    }
}
