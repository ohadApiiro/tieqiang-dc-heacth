package com.dchealth.service.rare;

import com.dchealth.VO.*;
import com.dchealth.entity.common.RoleVsUser;
import com.dchealth.entity.rare.YunDiseaseList;
import com.dchealth.facade.common.BaseFacade;
import com.dchealth.util.DateUtils;
import com.dchealth.util.ExcelUtil;
import com.dchealth.util.GroupQuerySqlUtil;
import com.dchealth.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/7/11.
 */
@Controller
@Path("statistics")
@Produces("application/json")
public class DiseaseCountService {
    @Autowired
    private BaseFacade baseFacade ;

    private static Map headMap = new HashMap();
    /**
     *根据医生id统计其研究病历的统计信息
     * @param doctorId
     * @return
     */
    @GET
    @Path("pat-disease-statis")
    public Response getDiseaseStatistics(@QueryParam("doctorId") String doctorId){
        List<String> dateList = getStaticsDate();
        List<DiseaseStatisVo> diseaseStatisVos = new ArrayList<>();
        //首先查询是否有管理病历信息
       Set<String> diseaseSet =  getManageDisease(doctorId);
       String  diseasHql = "select ydl from YunUserDisease yud ,YunDiseaseList ydl where ydl.dcode=yud.dcode ";
       String userIds = GroupQuerySqlUtil.getUserIds(doctorId,baseFacade);
       if(StringUtils.isEmpty(userIds)){
           diseasHql += " and yud.userId='"+doctorId+"'";
           //diseasHql += " and (yud.userId='"+doctorId+"'";
           //diseasHql += " or exists(select 1 from YunUserDiseaseManager ym where ym.dcode = ydl.dcode and ym.userId = '"+doctorId+"'))";
       }else{
           diseasHql += " and yud.userId  in ("+userIds+")";
           //diseasHql += " and (yud.userId  in ("+userIds+")";
           //diseasHql += " or exists(select 1 from YunUserDiseaseManager ym where ym.dcode = ydl.dcode and ym.userId in ("+userIds+")))";
       }
       List<YunDiseaseList>  yunDiseaseLists = baseFacade.createQuery(YunDiseaseList.class,diseasHql,new ArrayList<Object>()).getResultList();
        Map<String,Long>  discussMap = getPatNumberByType(doctorId,userIds,"0");//
        if(diseaseSet!=null && !diseaseSet.isEmpty()){
            Map<String,Long>  manageMap = getPatNumberByType(doctorId,userIds,"1");//
            discussMap.putAll(manageMap);
        }
       for(YunDiseaseList yunDiseaseList:yunDiseaseLists){
            DiseaseStatisVo diseaseStatisVo = new DiseaseStatisVo();
            String dcode = yunDiseaseList.getDcode();
            diseaseStatisVo.setDcode(dcode);
            diseaseStatisVo.setMonths(dateList);
            List<DcodeCountInfo> dcodeCountInfos = new ArrayList<>();
            for(String queryDate:dateList){
                DcodeCountInfo dcodeCountInfo = new DcodeCountInfo();
                dcodeCountInfo.setDate(queryDate);
                Long total = discussMap.get(queryDate+dcode)==null?0L:discussMap.get(queryDate+dcode);
                dcodeCountInfo.setTotal(total);//getDiseasCount(queryDate,dcode,doctorId,diseaseSet)
                dcodeCountInfos.add(dcodeCountInfo);
            }
            diseaseStatisVo.setDcodeCountInfos(dcodeCountInfos);
            diseaseStatisVos.add(diseaseStatisVo);
        }
        return Response.status(Response.Status.OK).entity(diseaseStatisVos).build();
    }

    public Map<String,Long> getPatNumberByType(String doctorId,String userIds,String type){
        Map<String,Long> retMap = new HashMap<String,Long>();
        String sql ="SELECT count(*) CT,f.diagnosis_code,DATE_FORMAT(f.create_date,'%Y-%m') from yun_patient p,yun_folder f where p.id = f.patient_id ";
        if("0".equals(type)){
            if(StringUtils.isEmpty(userIds)){
                sql +=" and p.doctor_id = '"+doctorId+"' ";
            }else{
                sql +=" and p.doctor_id in ("+userIds+") ";
            }
        }else if("1".equals(type)){
            if(StringUtils.isEmpty(userIds)){
                sql += " and exists(select 1 from yun_user_disease_manager ym where ym.dcode = f.diagnosis_code and ym.user_id = '"+doctorId+"')";
            }else{
                sql += " and exists(select 1 from yun_user_disease_manager ym where ym.dcode = f.diagnosis_code and ym.user_id in ("+userIds+"))";
            }
        }
        sql += " and p.doctor_id not in (select id from yun_users where rolename in ('FORM_USER','ADMINISTRATOR') and id<>'"+doctorId+"')";
        sql += " and f.create_date BETWEEN date_sub(date_sub(date_format(now(),'%y-%m-%d 00:00:00'),interval extract( day from now())-1 day),interval 5 month) and NOW() " +
                " GROUP BY f.diagnosis_code,DATE_FORMAT(f.create_date,'%Y-%m')";
        List list = baseFacade.createNativeQuery(sql).getResultList();
        if(list!=null && !list.isEmpty()){
            int size = list.size();
            for(int i=0;i<size;i++){
                Object[] params = (Object[])list.get(i);
                retMap.put(params[2]+""+params[1]+"",Long.parseLong(params[0].toString()));
            }
        }
        return retMap;
    }
    public Set getManageDisease(String doctorId){
        Set<String> set = new HashSet<>();
        String hql = "select ydl from YunUserDiseaseManager yud ,YunDiseaseList ydl where ydl.dcode=yud.dcode "+
                " and yud.userId='"+doctorId+"'" ;
        List<YunDiseaseList> yunDiseaseLists = baseFacade.createQuery(YunDiseaseList.class, hql, new ArrayList<Object>()).getResultList();
        for(YunDiseaseList yunDiseaseList:yunDiseaseLists){
            set.add(yunDiseaseList.getDcode());
        }
        return set;
    }
    /**
     *根据医生Id判断其是否有病人列表高级权限
     * @param doctorId
     * @return
     */
    public Boolean checkIsAdmin(String doctorId){
        Boolean isAdmin = false;
        String hql = " select rvu from RoleVsUser as rvu,RoleVsResource as rvr,ResourceDict as rd where " +
                " rvu.roleId = rvr.roleId and rvr.resourceId = rd.id and rd.code = 'SICK_PLUS' " +
                " and rvu.userId = '"+doctorId+"'";
        List<RoleVsUser> yunDiseaseLists = baseFacade.createQuery(RoleVsUser.class,hql,new ArrayList<Object>()).getResultList();
        if(yunDiseaseLists!=null && !yunDiseaseLists.isEmpty()){
            isAdmin = true;
        }
        return isAdmin;
    }

    /**
     * 根据日期和疾病编码查询该月份下的疾病数
     * @param queryDate
     * @param dcode
     * @return
     */
    public Long getDiseasCount(String queryDate,String dcode,String doctorId,Set diseaseSet){
        String [] dateArray = queryDate.split("-");
        String year = dateArray[0];
        String month = dateArray[1];
        String hql = "select count(p) from YunPatient as p,YunFolder as f where p.id = f.patientId and Year(f.createDate) = " +year+
                " and Month(f.createDate) = "+month+" and f.diagnosisCode = '"+dcode+"'";
        if(diseaseSet==null || !diseaseSet.contains(dcode)){
            hql += " and p.doctorId = '"+doctorId+"'";
        }
        return baseFacade.createQuery(Long.class,hql,new ArrayList<Object>()).getSingleResult();
    }

    /**
     * 获取要统计的月份信息(从当前月份向前推六个月)
     * @return
     */
    public List getStaticsDate(){
        List<String> dateList = new ArrayList<>();
        for(int i=0;i<6;i++){
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.MONTH,-i);
            int year = ca.get(Calendar.YEAR);
            int month = ca.get(Calendar.MONTH)+1;
            String dateStr = "";
            if(month<10){
                dateStr = year+"-"+"0"+month;
            }else{
                dateStr = year+"-"+month;
            }
            dateList.add(dateStr);
        }
        return dateList;
    }

    /**
     *获取数据共享各个疾病的病例数统计
     * @return
     */
    @GET
    @Path("get-disease-share-count")
    public List<DiseaseShareCount> getDiseaseShareCounts(){
        List<DiseaseShareCount> diseaseShareCounts = new ArrayList<>();
        String sql = "select count(p.id),f.diagnosis_code,(select name from yun_disease_list where dcode = f.diagnosis_code)" +
                " from yun_folder f,yun_patient p where f.patient_id = p.id and f.diagnosis_code !='' ";
        sql += " and p.doctor_id not in (select id from yun_users where rolename in ('FORM_USER','ADMINISTRATOR'))";
        sql += " group by f.diagnosis_code";
        List list = baseFacade.createNativeQuery(sql).getResultList();
        if(list!=null && !list.isEmpty()) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                DiseaseShareCount diseaseShareCount = new DiseaseShareCount();
                Object[] params = (Object[]) list.get(i);
                int count = Integer.valueOf(params[0].toString());
                diseaseShareCount.setCount(count);
                diseaseShareCount.setDcode(params[1]+"");
                diseaseShareCount.setDcodeName(params[2]+"");
                diseaseShareCounts.add(diseaseShareCount);
            }
        }
        return diseaseShareCounts;
    }

    /**
     * 获取疾病信息总共录入病历数，本月和上个月录入的病历数
     * @param name 疾病名称 模糊匹配
     * @param type 查询类型1为按医院统计 0为按病种统计
     * @param perPage 每页条数
     * @param currentPage 当前页
     * @return
     */
    @GET
    @Path("get-disease-month-count")
    public Page<DiseaseMonthCount> getDiseaseMonthCount(@QueryParam("name")String name, @QueryParam("type")String type,
                                                        @QueryParam("perPage") int perPage,@QueryParam("currentPage")int currentPage){
        Map<String,String> nowDataMap = getNeedDateStr("1");
        Map<String,String> lastDataMap = getNeedDateStr("0");
        Page<DiseaseMonthCount> diseaseMonthCountPage = new Page<DiseaseMonthCount>();
        String hql = "";
        String hqlCount = "";
        if("1".equals(type)){//类型为1按医院统计
            hql ="select new com.dchealth.VO.DiseaseMonthCount(d.hospitalName as name,d.hospitalCode as dcode,(select count(yf.id) from YunFolder as yf,YunPatient as yp,YunUsers as u,YunDiseaseList as yd where " +
                    " yf.diagnosisCode = yd.dcode and yf.patientId = yp.id and yp.doctorId = u.id and u.rolename!='FORM_USER' and u.hospitalName = d.hospitalName ) as totalCount," +
                    "(select count(yf.id) from YunFolder as yf,YunPatient as yp,YunUsers as u where u.hospitalName = d.hospitalName and " +
                    "yf.patientId = yp.id and yp.doctorId = u.id and u.rolename!='FORM_USER' " +
                    " and yp.createDate>='"+nowDataMap.get("firstDay")+"' and yp.createDate<='"+nowDataMap.get("lastDay")+"' " +
                    ") as nowMonthCount,(select count(yf.id) from YunFolder as yf,YunPatient as yp,YunUsers as u where u.hospitalName = d.hospitalName and " +
                    "yf.patientId = yp.id and yp.doctorId = u.id and u.rolename!='FORM_USER'" +
                    "and yp.createDate>='"+lastDataMap.get("firstDay")+"' and yp.createDate<='"+lastDataMap.get("lastDay")+"' " +
                    ") as lastMonthCount,(select count(yf.id) from YunFolder as yf,YunPatient as yp,YunUsers as u,HospitalDict as hd where " +
                    " yf.patientId = yp.id and yp.doctorId = u.id and u.rolename!='FORM_USER' and u.hospitalName = hd.hospitalName ) as sumTotalCount) from HospitalDict as d" +
                    " where d.status<>'-1' ";
            hqlCount = "select count(*) from HospitalDict as d where d.status<>'-1' ";
            if(!StringUtils.isEmpty(name)){
                hql += " and d.hospitalName like '%"+name+"%'";
                hqlCount += " and d.hospitalName like '%"+name+"%'";
            }
            hql += " and exists(select 1 from YunFolder as yf,YunPatient as yp,YunUsers as u where yf.patientId = yp.id " +
                    " and yp.doctorId = u.id and u.rolename!='FORM_USER' and u.hospitalName = d.hospitalName) order by totalCount desc ";
            hqlCount += " and exists(select 1 from YunFolder as yf,YunPatient as yp,YunUsers as u where yf.patientId = yp.id " +
                    " and yp.doctorId = u.id and u.rolename!='FORM_USER' and u.hospitalName = d.hospitalName) ";
        }else{//类型为0按疾病统计
            hql ="select new com.dchealth.VO.DiseaseMonthCount(d.name,d.dcode,count(f.id) as totalCount," +
                    "(select count(yf.id) from YunFolder as yf,YunPatient as yp,YunUsers as u where yf.diagnosisCode = d.dcode and " +
                    "yf.patientId = yp.id and yp.doctorId = u.id and u.rolename!='FORM_USER' " +
                    " and yp.createDate>='"+nowDataMap.get("firstDay")+"' and yp.createDate<='"+nowDataMap.get("lastDay")+"' " +
                    "group by yf.diagnosisCode) as nowMonthCount,(select count(yf.id) from YunFolder as yf,YunPatient as yp,YunUsers as u where yf.diagnosisCode = d.dcode and " +
                    "yf.patientId = yp.id and yp.doctorId = u.id and u.rolename!='FORM_USER'" +
                    "and yp.createDate>='"+lastDataMap.get("firstDay")+"' and yp.createDate<='"+lastDataMap.get("lastDay")+"' " +
                    "group by yf.diagnosisCode) as lastMonthCount,(select count(yf.id) from YunFolder as yf,YunPatient as yp,YunUsers as u,YunDiseaseList as yd where yf.diagnosisCode = yd.dcode and " +
                    "yf.patientId = yp.id and yp.doctorId = u.id and u.rolename!='FORM_USER') as sumTotalCount) from YunDiseaseList as d,YunFolder as f,YunPatient as p" +
                    " where d.dcode = f.diagnosisCode and f.patientId = p.id  and  exists(select 1 from YunUsers where id = p.doctorId and rolename!='FORM_USER')";
            hqlCount = "select count(f.id) from YunDiseaseList as d,YunFolder as f,YunPatient as p " +
                    " where d.dcode = f.diagnosisCode and f.patientId = p.id  and  exists(select 1 from YunUsers where id = p.doctorId and rolename!='FORM_USER')";
            if(!StringUtils.isEmpty(name)){
                hql += " and d.name like '%"+name+"%'";
                hqlCount += " and d.name like '%"+name+"%'";
            }
            hql += " group by d.id order by totalCount desc";
        }
        Long aLong = baseFacade.createQuery(Long.class, hqlCount, new ArrayList<Object>()).getSingleResult();
        TypedQuery<DiseaseMonthCount> pfBaseTypedQuery = baseFacade.createQuery(DiseaseMonthCount.class, hql, new ArrayList<Object>());
        if(perPage<1){
            perPage = 20;
        }
        diseaseMonthCountPage.setCounts(aLong);
        diseaseMonthCountPage.setPerPage((long)perPage);
        if(currentPage<=0){
            currentPage = 1;
        }
        if(perPage>0){
            pfBaseTypedQuery.setFirstResult((currentPage-1)*perPage) ;
            pfBaseTypedQuery.setMaxResults(perPage);
        }
        List<DiseaseMonthCount> diseaseMonthCountList = pfBaseTypedQuery.getResultList();
        diseaseMonthCountPage.setData(diseaseMonthCountList);
        return diseaseMonthCountPage;
    }

    /**
     * 导出统计数据为excel
     * @param type 类型 1为按医院统计 0为按病例统计
     * @param perPage 每页条数
     * @param currentPage 当前页
     * @param name 名称
     * @param httpServletResponse
     * @param httpServletRequest
     * @return
     */
    @GET
    @Path("export-excel-data")
    public List<String> getCountInfoExcel(@QueryParam("type")String type, @QueryParam("perPage")int perPage, @QueryParam("currentPage")int currentPage, @QueryParam("name")String name,
                                          @Context HttpServletResponse httpServletResponse,@Context HttpServletRequest httpServletRequest){
        List<String> list = new ArrayList<>();
        List<DiseaseMonthCount> diseaseMonthCountList = null;
        Map<String,String> nowDataMap = getNeedDateStr("1");
        Map<String,String> lastDataMap = getNeedDateStr("0");
        if(perPage>0 && currentPage>0){
            Page<DiseaseMonthCount> diseaseMonthCountPage = getDiseaseMonthCount(name,type,perPage,currentPage);
            diseaseMonthCountList = diseaseMonthCountPage.getData();
        }else{
            String hql = "";
            if("1".equals(type)){
                 hql ="select new com.dchealth.VO.DiseaseMonthCount(d.hospitalName as name,d.hospitalCode as dcode,(select count(yf.id) from YunFolder as yf,YunPatient as yp,YunUsers as u,YunDiseaseList as yd where " +
                        " yf.diagnosisCode = yd.dcode and yf.patientId = yp.id and yp.doctorId = u.id and u.rolename!='FORM_USER' and u.hospitalName = d.hospitalName ) as totalCount," +
                        "(select count(yf.id) from YunFolder as yf,YunPatient as yp,YunUsers as u where u.hospitalName = d.hospitalName and " +
                        "yf.patientId = yp.id and yp.doctorId = u.id and u.rolename!='FORM_USER' " +
                        " and yp.createDate>='"+nowDataMap.get("firstDay")+"' and yp.createDate<='"+nowDataMap.get("lastDay")+"' " +
                        ") as nowMonthCount,(select count(yf.id) from YunFolder as yf,YunPatient as yp,YunUsers as u where u.hospitalName = d.hospitalName and " +
                        "yf.patientId = yp.id and yp.doctorId = u.id and u.rolename!='FORM_USER'" +
                        "and yp.createDate>='"+lastDataMap.get("firstDay")+"' and yp.createDate<='"+lastDataMap.get("lastDay")+"' " +
                        ") as lastMonthCount) from HospitalDict as d" +
                        " where d.status<>'-1' ";
                if(!StringUtils.isEmpty(name)){
                    hql += " and d.hospitalName like '%"+name+"%'";
                }
                hql += " and exists(select 1 from YunFolder as yf,YunPatient as yp,YunUsers as u where yf.patientId = yp.id " +
                        " and yp.doctorId = u.id and u.rolename!='FORM_USER' and u.hospitalName = d.hospitalName) order by totalCount desc ";
            }else{
                hql ="select new com.dchealth.VO.DiseaseMonthCount(d.name,d.dcode,count(f.id) as totalCount," +
                        "(select count(yf.id) from YunFolder as yf,YunPatient as yp,YunUsers as u where yf.diagnosisCode = d.dcode and " +
                        "yf.patientId = yp.id and yp.doctorId = u.id and u.rolename!='FORM_USER' " +
                        " and yp.createDate>='"+nowDataMap.get("firstDay")+"' and yp.createDate<='"+nowDataMap.get("lastDay")+"' " +
                        "group by yf.diagnosisCode) as nowMonthCount,(select count(yf.id) from YunFolder as yf,YunPatient as yp,YunUsers as u where yf.diagnosisCode = d.dcode and " +
                        "yf.patientId = yp.id and yp.doctorId = u.id and u.rolename!='FORM_USER'" +
                        "and yp.createDate>='"+lastDataMap.get("firstDay")+"' and yp.createDate<='"+lastDataMap.get("lastDay")+"' " +
                        "group by yf.diagnosisCode) as lastMonthCount) from YunDiseaseList as d,YunFolder as f,YunPatient as p" +
                        " where d.dcode = f.diagnosisCode and f.patientId = p.id  and  exists(select 1 from YunUsers where id = p.doctorId and rolename!='FORM_USER')";
                if(!StringUtils.isEmpty(name)){
                    hql += " and d.name like '%"+name+"%'";
                }
                hql += " group by d.id order by totalCount desc";
            }
            diseaseMonthCountList = baseFacade.createQuery(DiseaseMonthCount.class,hql,new ArrayList<Object>()).getResultList();
        }
        if(diseaseMonthCountList!=null && !diseaseMonthCountList.isEmpty()){
            if("1".equals(type)){
                headMap.put("name","单位");
            }else{
                headMap.put("name","疾病名称");
            }
            headMap.put("totalCount","病例总数");
            headMap.put("nowMonthCount","本月录入数");
            headMap.put("lastMonthCount","上月录入数");
            String filePath = ExcelUtil.produceExcel(headMap,diseaseMonthCountList,httpServletRequest);
            String diseaseName = "1".equals(type)?"医院统计信息":"病例统计信息";
            String fileName = diseaseName+ DateUtils.getFormatDate(new Date(),DateUtils.DATE_FORMAT)+".xlsx";
            ExcelUtil.download(filePath,httpServletResponse,fileName);
        }
        return list;
    }
    /**
     * 根据类型查询本月和上个月第一天和最后一天的时间
     * @param type
     * @return
     */
    public Map<String,String> getNeedDateStr(String type){
        Map<String,String> dateMap = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if("0".equals(type)){//获取上个月一号到月末日期
            Calendar cal_1=Calendar.getInstance();//获取当前日期
            cal_1.add(Calendar.MONTH, -1);
            cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
            cal_1.set(Calendar.HOUR_OF_DAY,0);
            cal_1.set(Calendar.MINUTE,0);
            cal_1.set(Calendar.SECOND,0);
            String firstDay = format.format(cal_1.getTime());
            //获取前月的最后一天
            Calendar cale = Calendar.getInstance();
            cale.set(Calendar.DAY_OF_MONTH,0);
            cale.set(Calendar.HOUR_OF_DAY,23);
            cale.set(Calendar.MINUTE,59);
            cale.set(Calendar.SECOND,59);
            String lastDay = format.format(cale.getTime());
            dateMap.put("firstDay",firstDay);
            dateMap.put("lastDay",lastDay);
        }else{
            //获取当前月第一天：
            Calendar c = Calendar.getInstance();
            String last = format.format(c.getTime());
            c.add(Calendar.MONTH, 0);
            c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
            c.set(Calendar.HOUR_OF_DAY,0);
            c.set(Calendar.MINUTE,0);
            c.set(Calendar.SECOND,0);
            String first = format.format(c.getTime());
            dateMap.put("firstDay",first);
            dateMap.put("lastDay",last);
        }
        return dateMap;
    }
}
