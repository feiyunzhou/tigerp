package com.rex.crm;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.template.PackageTextTemplate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rex.crm.beans.Account;
import com.rex.crm.beans.Choice;
import com.rex.crm.beans.Contact;
import com.rex.crm.common.Entity;
import com.rex.crm.db.DAOImpl;
import com.rex.crm.util.Configuration;


/**
 * @author Feiyun Zhou 
 */
public class CreateEventPage extends TemplatePage
{
    
    private static final Logger logger = Logger.getLogger(CreateEventPage.class);
    public Date startDate;
    protected String endDate;
    protected String endTime;
    protected String startTime;
    protected String hidden_contact_select;
    protected Choice visiting_purpose = new Choice(1L,"");
    protected Choice feature_product = new Choice(1L,"");;
    protected Choice activity_type = new Choice(1L,"");;
    protected String act_title_input = "";
	/**
	 * Constructor
	 */
	public CreateEventPage()
	{
	    

		Map<String, Entity> entities = Configuration.getEntityTable();
        Entity entity = entities.get("activity");
        setPageTitle(entity.getDisplay());
        final String uid = ((SignIn2Session)getSession()).getUserId();
        
        Form form = new Form("form"){
            @Override
            protected void onSubmit()
            {
                logger.debug("the form was submitted!");
                
                //logger.debug("startDate:"+  startDate);
                String sd = getRequest().getPostParameters().getParameterValue("start_date").toString();
                String st = getRequest().getPostParameters().getParameterValue("start_time").toString();
                String ed = getRequest().getPostParameters().getParameterValue("end_date").toString();
                String et = getRequest().getPostParameters().getParameterValue("end_time").toString();
                //String visit_type = getRequest().getPostParameters().getParameterValue("visit_type").toString();
                String visit_type = String.valueOf(activity_type);
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String sdt = sd+ " " +st;
                String edt = ed + " " + et;
                Date startdt = null;
                Date enddt = null;
                try {
                     startdt = dateformat.parse(sdt);
                     enddt = dateformat.parse(edt);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                logger.debug(String.format("time info %s %s %s %s", sd,st,ed,et));
                
                logger.debug("contact id:"+ hidden_contact_select);
                logger.debug("visit_type:" + visit_type);
                try {
                    
                    DAOImpl.addCalendarEvent(Integer.parseInt(uid), Integer.parseInt(hidden_contact_select), visit_type, act_title_input, String.valueOf(startdt.getTime()/1000), String.valueOf(enddt.getTime()/1000), 1);
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                setResponsePage(PageFactory.createPage("calendar"));
                
            }
        };
        add(form);
        
        
         StringValue startdateValue = this.getRequest().getRequestParameters().getParameterValue("startdate");
         
         String startdate  = null;
         long current = System.currentTimeMillis();
         Date current_date_time = new Date(current);
         SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
         //set default time to current time
         if(startdateValue != null && !startdateValue.isEmpty() && !startdateValue.isNull()){
              startdate = startdateValue.toString();
         }else{     
             startdate = dateformat.format(current_date_time);
         }
         logger.debug("startdate:"+startdate);
         
         
         
         
         WebMarkupContainer start_date_input = new WebMarkupContainer("start_date_input");
         //startDate = startdate;
         //DateTextField start_date_input = new DateTextField("start_date_input", new PropertyModel<Date>(this,"startDate"));
         form.add(start_date_input);
         start_date_input.add(new AttributeModifier("value",startdate));
         
         SimpleDateFormat timeformatter = new SimpleDateFormat("HH:mm");
         WebMarkupContainer start_time_input = new WebMarkupContainer("start_time_input");
         form.add(start_time_input);
         start_time_input.add(new AttributeModifier("value",timeformatter.format(current_date_time)));
         
         SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
         
         Date sDate = current_date_time;
         try {
             sDate = dateformat2.parse(startdate+ " " + timeformatter.format(current_date_time));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            logger.warn("failed to parse date:",e);
        }
         
         
         Date next_date_time = new Date(sDate.getTime()+3600*1000);
         WebMarkupContainer end_date_input = new WebMarkupContainer("end_date_input");
         form.add(end_date_input);
         end_date_input.add(new AttributeModifier("value",dateformat.format(next_date_time)));
         
         

         
         WebMarkupContainer end_time_input = new WebMarkupContainer("end_time_input",new Model(""));
         form.add(end_time_input);
         end_time_input.add(new AttributeModifier("value",timeformatter.format(next_date_time)));
              
         
         PopupSettings popupSettings = new PopupSettings("查找").setHeight(470)
                 .setWidth(850).setLeft(150).setTop(200);
         form.add(new BookmarkablePageLink<Void>("search_btn", SearchContactPage.class).setPopupSettings(popupSettings));


         form.add(new HiddenField("hidden_contact_select" ,new PropertyModel<String>(this,"hidden_contact_select")));
         form.add(new TextField("contact_select", new Model("")));
        
         
         final List<Choice> sales_visiting_purpose_pl = DAOImpl.queryPickList("sales_visiting_purpose_pl");
         final List<Choice> com_visiting_purpose_pl = DAOImpl.queryPickList("com_visiting_purpose_pl");
         
         
         IModel<List>  visiting_purpose_choices_model = new AbstractReadOnlyModel<List>()
                 {

                    @Override
                    public List getObject() {
                        List<Choice> choices =  new ArrayList<Choice>();
                        if(activity_type.getId() == 1L){
                            return com_visiting_purpose_pl;
                        }else{
                            return sales_visiting_purpose_pl;
                        }
                    }
                   

                 };

          //visiting purpose choice
        final DropDownChoice visiting_purpose_choice = createDropDownListFromPickList("visiting_purpose_input","com_visiting_purpose_pl",visiting_purpose_choices_model,new PropertyModel(this,"visiting_purpose"));
        visiting_purpose_choice.setOutputMarkupId(true); 
        form.add(visiting_purpose_choice);
         
         //event type choice
         DropDownChoice activity_type_choice = createDropDownListFromPickList("activity_type_input","activity_activity_types_pl",null,new PropertyModel(this,"activity_type"));
         activity_type_choice.setOutputMarkupId(true);
         form.add(activity_type_choice);
 
         activity_type_choice.add(new AjaxFormComponentUpdatingBehavior("onchange")
         {
           
            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                
//                if(activity_type.getId() == 1L){
//
//                  visiting_purpose_choice.setChoices(com_visiting_purpose_pl);
//                }else{
//                  
//                    visiting_purpose_choice.setChoices(sales_visiting_purpose_pl);
//                }
                target.add(visiting_purpose_choice);
            }
         });
         

         form.add(createDropDownListFromPickList("feature_product_input","activity_feature_product_pl",null,new PropertyModel(this,"feature_product")));
         form.add(new TextField("act_title_input", new PropertyModel(this,"act_title_input")));
        
        
	}
	

	
	private DropDownChoice createDropDownListFromPickList(String markupId, String picklistName,IModel choices,PropertyModel default_model){
	    
	  if(choices == null){ 
	       choices = Model.ofList(DAOImpl.queryPickList(picklistName));
	  }
	   
	  return new DropDownChoice<Choice>(markupId, default_model, choices,
                new IChoiceRenderer<Choice>() {

                    @Override
                    public Object getDisplayValue(Choice choice) {
                        // TODO Auto-generated method stub
                        return choice.getVal();
                    }

                    @Override
                    public String getIdValue(Choice choice, int index) {
                        // TODO Auto-generated method stub
                        return String.valueOf(choice.getId());
                    }

                });
	}
	
	class SelectOption implements Serializable{
	    private int key;
	    private String value;
	   
	    public SelectOption(int key, String value) {
	      this.key = key;
	      this.value = value;
	    }
	    public SelectOption() {
	          
	        }

	    public int getKey() {
	        return key;
	    }

	    public void setKey(int key) {
	        this.key = key;
	    }

	    public String getValue() {
	        return value;
	    }

	    public void setValue(String value) {
	        this.value = value;
	    }
	  }



}



