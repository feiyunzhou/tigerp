package com.rex.crm.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.AbstractItem;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.common.collect.Maps;
import com.rex.crm.AccountPage;
import com.rex.crm.ActivityPage;
import com.rex.crm.CoachingPage;
import com.rex.crm.ContactPage;
import com.rex.crm.SignIn2Session;
import com.rex.crm.TemplatePage;
import com.rex.crm.admin.AdminTreePage;
import com.rex.crm.admin.PositionPage;
import com.rex.crm.admin.ProductPage;
import com.rex.crm.admin.ProductTreePage;
import com.rex.crm.admin.UserPage;
import com.rex.crm.beans.AccountCRMUserRelation;
import com.rex.crm.beans.CRMUser;
import com.rex.crm.beans.UserInfo;
import com.rex.crm.beans.UserPosition;
import com.rex.crm.db.DAOImpl;
import com.rex.crm.util.CRMUtility;
import com.rex.crm.util.Configuration;
import com.rex.crm.util.SendEmail;

public class EntityDetailContainerPanel   extends Panel {
    private static final Logger logger = Logger.getLogger(EntityDetailContainerPanel.class);
    private static final long serialVersionUID = -2613412283023068638L;
    private final String user = ((SignIn2Session)getSession()).getUser();
    private static int NUM_OF_COLUMN  = 3;
    public EntityDetailContainerPanel(String id){
        super(id);

    }
    public EntityDetailContainerPanel(String panelId, final String entityName, final String id,final Class previousPageClass, final PageParameters prePageParams){
        this(panelId);
    	initPage(entityName,id,previousPageClass,prePageParams);
    }
    public void initPage(final String entityName, final String id ,final Class previousPageClass, final PageParameters prePageParams){
       
        final int roleId = ((SignIn2Session)getSession()).getRoleId();
        Map<String, Entity> entities = Configuration.getEntityTable();
        Entity entity = entities.get(entityName);
        final RepeatingView div = new RepeatingView("promptDiv");
        final AbstractItem groupitem = new AbstractItem(div.newChildId());
        final Label promptButton = new Label("promptButton","X");
        groupitem.add(promptButton);
        final Label promptLabel = new Label("prompt","提示:操作已成功！");
        groupitem.add(promptLabel);
        div.add(new AttributeAppender("style",new Model("display:none"),";"));
        groupitem.add(new AttributeAppender("style",new Model("display:none"),";"));
        div.add(groupitem);
        add(div);
        /*final Label promptButton = new Label("promptButton","X");
        final Label promptLabel = new Label("prompt","提示:操作已成功！");
        add(promptButton);
        add(promptLabel);*/
        
        long lid = Long.parseLong(id);
         System.out.println("*entityName:"+entityName);
         System.out.println("*id:"+id);
       // Map map = DAOImpl.getEntityData(entity.getName(), entity.getFieldNames(), lid);
        Map map = DAOImpl.queryEntityById(entity.getSql_ent(), String.valueOf(lid));
        if(entity.getName().equals("activity")||entity.getName().equals("coaching")||entity.getName().equals("willcoaching")){
        	add(new Label("name",String.valueOf(map.get("title"))));
        }else{
        	add(new Label("name",String.valueOf(map.get("name"))));
        }
        
        add(new EntityDetailPanel("detailed",entity,map,id,3,entityName));
        
        
        
        if(entityName.equalsIgnoreCase("productLine")||entityName.equalsIgnoreCase("product")||entityName.equalsIgnoreCase("productSpecification")){
         List<Relation> relations = Configuration.getRelationsByName(entityName);
         
         RepeatingView relationRepeater = new RepeatingView("relationRepeater");
         add(relationRepeater);
         
         List<Field> paramFields = entity.getParamFields();
         Map<String,Object> params = Maps.newHashMap();
         for(Field f:paramFields){
             params.put(entityName+"."+f.getName(), map.get(f.getName()));
         }
         
         
         for(Relation r:relations){
           AbstractItem item = new AbstractItem(relationRepeater.newChildId());
           relationRepeater.add(item);
           logger.debug(r.getSql());
           logger.debug("parms:"+id);
           List list = DAOImpl.queryEntityRelationList(r.getSql(), id);
           item.add(new RelationDataPanel("relationPanel",r,entityName,list,String.valueOf(lid),params));
          }
        }else{
            WebMarkupContainer con =new WebMarkupContainer("relationRepeater");
            add(con);
            con.add(new EmptyPanel("relationPanel"));
            con.setVisible(false);
            
        }
        
        

         if(entityName.equalsIgnoreCase("account")){
             add(new TeamManPanel("teamPanel",entityName,String.valueOf(lid),0));
             add(new EmptyPanel("teamPanel2"));
             add(new EmptyPanel("teamPanel4"));
             add(new EmptyPanel("teamPanel5"));
         }else if(entityName.equalsIgnoreCase("crmuser")){
        	 add(new EmptyPanel("teamPanel"));
             add(new TeamManPanel("teamPanel2",entityName,String.valueOf(lid),2));
             add(new TeamManPanel("teamPanel4",entityName,String.valueOf(lid),3));
             add(new TeamManPanel("teamPanel5",entityName,String.valueOf(lid),4));
         }
         else{
             add(new EmptyPanel("teamPanel"));
             add(new EmptyPanel("teamPanel2"));
             add(new EmptyPanel("teamPanel4"));
             add(new EmptyPanel("teamPanel5"));
         }

         add(new AbstractAjaxBehavior(){

            @Override
            public void onRequest() {
            }

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                response.render(OnDomReadyHeaderItem.forScript("$(\"#navitem-"+entityName+"\").addClass(\"active\");"));
            }  
             
         });
         
        // final Page this_page = previousePage;
         ICRUDActionListener actionListener = new DefaultCRUDActionListener(){

            @Override
            public void delete() {
                if(entityName.equals("account")){
                    DAOImpl.deleteRecord(id, entityName);
                    setResponsePage(new AccountPage());
                }else if(entityName.equals("contact")){
                    DAOImpl.deleteRecord(id, entityName);
                    setResponsePage(new ContactPage());
                }else if(entityName.equals("activity")) {
                    DAOImpl.deleteRecord(id, entityName);
                    
                    setResponsePage(new ActivityPage());
                }else if(entityName.equals("coaching")) {
                  DAOImpl.deleteRecord(id, entityName);
                  setResponsePage(new CoachingPage());
                }else if(entityName.equalsIgnoreCase("userInfo")){
                    if(DAOImpl.deleteRecord(id, entityName)>0){
                       DAOImpl.updateCrmUserReport(id, "-1");
                    }
                    setResponsePage(new UserPage());
                }else if(entityName.equals("crmuser")) {
                    String reportto = String.valueOf(DAOImpl.getReporttoIdById(id));
                    DAOImpl.deleteRecord(id, entityName);
                   if(reportto !=null ){
                	   setResponsePage(new AdminTreePage(reportto));  
                   } else{
                	   setResponsePage(new AdminTreePage());
                   }
//                    setResponsePage(new PositionPage());
                }else if(entityName.equals("productcategory")){
                	DAOImpl.deleteRecord(id, entityName);
                	setResponsePage(new ProductTreePage());
                }else if(entityName.equals("product")){
                 	DAOImpl.deleteProductRecord(id, entityName);
                	setResponsePage(new ProductTreePage());
                }else if(entityName.equals("productline")){
                	DAOImpl.deleteProductLineRecord(id, entityName);
                	setResponsePage(new ProductTreePage());
                }
            }

            @Override
            public void update() {
                if(entityName.equals("product")||entityName.equals("productline")||entityName.equals("procuctcategory")){
                   prePageParams.add("entityName", entityName);
                }
                setResponsePage(new EditDataPage(entityName,id,previousPageClass,prePageParams));
                System.out.println("EntityDetailContainerPanel:"+prePageParams);
            }
            @Override
            public void doneBtn(){
              final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
              Date time = new Date();
              String d = dateformat.format(time);
              DAOImpl.doneRecord(id, entityName, d);
            }
            @Override
            public void resetPassword(int userId){
            	if(DAOImpl.resetUserPassword(userId)>0){
            		//获取对象
                	UserInfo crmuser = DAOImpl.getUserInfoById(userId);
                	//发送邮件,判断成功与否
                	if(SendEmail.sendMail(String.valueOf(crmuser.getId()),crmuser.getEmail())){
                		div.add(new AttributeAppender("style",new Model("display:block"),";"));
                		groupitem.add(new AttributeAppender("style",new Model("display:block"),";"));
                		promptLabel.add(new AttributeAppender("style",new Model("display:block"),";"));
                		promptButton.add(new AttributeAppender("style",new Model("display:block"),";"));
                	};
                	/*if(sendMail(crmuser.getLoginName(),crmuser.getEmail())){
                		//promptLabel.add(new AttributeAppender("style",new Model("display:block"),";"));
                		setResponsePage(new UserPage());
                	};*/
            	};
            }

            @Override
            public void downLoadBtn() throws Exception
            {
              // TODO Auto-generated method stub
              
            }

			@Override
			public void merge() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void ineffective() {
				// TODO Auto-generated method stub
				List<UserPosition> users = new ArrayList<UserPosition>();
				
				if(entityName.equalsIgnoreCase("userinfo")){
					UserPosition userInfo = DAOImpl.getUserPositionById(Integer.parseInt(id));
					DAOImpl.updateUserInfoPositionByUserId(entityName,id);
					DAOImpl.removeEntityFromTeam("user_position",String.valueOf(id));
					DAOImpl.insertRealtionHestory("user_position",user,userInfo.getPositionId(),Integer.parseInt(id));
				}else if(entityName.equalsIgnoreCase("crmuser"))
					try {
						{
							DAOImpl.updateUserInfoPositionByUserId(entityName,id);
							users = DAOImpl.getUsersByPositionId(id);
							List<CRMUser> crmusers = DAOImpl.getPositionByReporttoId(id);
							DAOImpl.updateCrmUserReport(id, "-1");
							CRMUser reporttoCrmuser = DAOImpl.getCrmUserById(id);
							for(CRMUser crmuser :crmusers){
								DAOImpl.insertAudit("crmuser","上级岗位",reporttoCrmuser.getName(),"admin",String.valueOf(crmuser.getId()),user);
							}
							List<AccountCRMUserRelation> acrs =  DAOImpl.getAccountsByPositionId(Integer.parseInt(id));
							for(AccountCRMUserRelation acr : acrs){
								DAOImpl.removeEntityFromTeam("accountcrmuser",String.valueOf(acr.getId()));
								DAOImpl.insertRealtionHestory("accountcrmuser",user,acr.getCrmuserId(),acr.getAccountId());
							}
							for(UserPosition userinfo : users){
								DAOImpl.removeEntityFromTeam("user_position",String.valueOf(userinfo.getId()));
								DAOImpl.insertRealtionHestory("user_position",user,userinfo.getPositionId(),userinfo.getUserId());
							}
						}
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else if(entityName.equalsIgnoreCase("account")){
					List<AccountCRMUserRelation> acrs =  DAOImpl.getAccountsByAccountId((Integer.parseInt(id)));
					for(AccountCRMUserRelation acr : acrs){
						DAOImpl.removeEntityFromTeam("accountcrmuser",String.valueOf(acr.getId()));
						DAOImpl.insertRealtionHestory("accountcrmuser",user,acr.getCrmuserId(),acr.getAccountId());
					}
				}
				setResponsePage(new AdminTreePage());
			}
            
         };
         

         add(new CRUDPanel("operationBar",entity.getName(),id, CRMUtility.getPermissionForEntity(roleId, entity.getName()),actionListener));
         
    }

}
