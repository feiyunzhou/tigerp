package com.rex.crm.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.gson.Gson;
import com.rex.crm.beans.Account;
import com.rex.crm.beans.CRMUser;
import com.rex.crm.beans.CalendarEvent;
import com.rex.crm.beans.City;
import com.rex.crm.beans.Contact;
import com.rex.crm.common.CRUDPanel;
import com.rex.crm.common.IFormatter;
import com.rex.crm.common.NewDataFormPanel;
import com.rex.crm.common.CRUDPanel.Permissions;
import com.rex.crm.userlog.LogInOut;
import com.rex.crm.userlog.LogObj;

public class CRMUtility {

       private static final Logger logger = Logger.getLogger(CRMUtility.class);
    
       public static final String STAT_LOG_IN_OUT = "logInOut";
    
	   public static ImmutableListMultimap<Integer, Account> categorizeAccountsByCityIds(List<Account> accounts){
	    	Function<Account, Integer> idFunction = new Function<Account, Integer>() {
				public Integer apply(Account account) {
					return account.getCityId();
				}
			};
			
			ImmutableListMultimap<Integer, Account> accountsByCityIds = Multimaps.index(accounts, idFunction);
	       
			return accountsByCityIds;
	    }
	   
	   public static ImmutableListMultimap<Integer, CRMUser> categorizeCRMUsersByCityIds(List<CRMUser> users){
	    	Function<CRMUser, Integer> idFunction = new Function<CRMUser, Integer>() {
				public Integer apply(CRMUser user) {
					return user.getCityId();
				}
			};
			
			ImmutableListMultimap<Integer, CRMUser> usersByCityIds = Multimaps.index(users, idFunction);
	       
			return usersByCityIds;
	    }
	   
	     
	    public static ImmutableListMultimap<Integer,City> categorizeCitiesByProvinceIds(List<City> cities){
	    	Function<City, Integer> idFunction = new Function<City, Integer>() {
				public Integer apply(City city) {
					return city.getProvinceId();
				}
			};

			
			ImmutableListMultimap<Integer, City> citiesByProvinceId = Multimaps
					.index(cities, idFunction);
			return citiesByProvinceId;
	    }
	    
	    
	    public static ImmutableListMultimap<Integer,Contact> categorizeContactsByAccountId(List<Contact> contacts){
	            Function<Contact, Integer> idFunction = new Function<Contact, Integer>() {
	                public Integer apply(Contact contact) {
	                    return contact.getAccountId();
	                }
	            };

	            
	            ImmutableListMultimap<Integer, Contact> contactsByAccountId = Multimaps.index(contacts, idFunction);
	            return contactsByAccountId;
	    }
	    
	    
	  public static ImmutableListMultimap<Integer,Pair<Integer,Integer>> categorizeEntitiesByExternalId(List<Pair<Integer,Integer>> pairs){
               Function<Pair<Integer,Integer>, Integer> idFunction = new Function<Pair<Integer,Integer>, Integer>() {
                   public Integer apply(Pair<Integer,Integer> pair) {
                       return pair.getRight();
                   }
               };

               
               ImmutableListMultimap<Integer, Pair<Integer,Integer>> entitiesByExternalId = Multimaps.index(pairs, idFunction);
               return entitiesByExternalId;
       }
	    
	    
	    
	    public static ImmutableListMultimap<Integer,CalendarEvent> categorizeEventsByUserIds(List<CalendarEvent> events){
	    	Function<CalendarEvent, Integer> idFunction = new Function<CalendarEvent, Integer>() {
				public Integer apply(CalendarEvent event) {
					return event.getCrmUserId();
				}
			};

			ImmutableListMultimap<Integer, CalendarEvent> eventsByUserId = Multimaps.index(events, idFunction);
			return eventsByUserId;
	    }
	    
	    
	    public static List<Account> getAccountsByIds(Map<String,Account> accountTable, final List<String> accountIds){
	    	Predicate<String> filters = new Predicate<String>() {
				  public boolean apply(String id) {
					  return accountIds.contains(id);
				  }
				};
				
			return Lists.newArrayList(Maps.filterKeys(accountTable, filters).values());
	    }
	    
	    
	    public static List<CRMUser> getCRMUsersByIds(Map<String,CRMUser> crmUserTable, final List<String> userIds){
	    	Predicate<String> filters = new Predicate<String>() {
				  public boolean apply(String id) {
					  return userIds.contains(id);
				  }
				};
				
			return Lists.newArrayList(Maps.filterKeys(crmUserTable, filters).values());
	    }
	    
	    
	    public static List filterObjectsByIds(Map idMappingTable, final List<Integer> ids){
	    	Predicate<String> filters = new Predicate<String>() {
				  public boolean apply(String id) {
					  return ids.contains(id);
				  }
				};
				
			return Lists.newArrayList(Maps.filterKeys(idMappingTable, filters).values());
	    }
	    
	    public static Map<Integer, City> filterCitiesWithIds(ImmutableMap<Integer, City> cityTable,final ImmutableMultiset<Integer> cityIds){
			Predicate<Integer> filters = new Predicate<Integer>() {
				  public boolean apply(Integer id) {
					  return cityIds.contains(id);
				  }
				};
			
			return Maps.filterKeys(cityTable, filters);
	    }

	    
	    public static List<CalendarEvent> filterEventsByUserIds(Map<Integer,CalendarEvent> idMappingTable, final List<Integer> userIds){
	    	Predicate<CalendarEvent> filters = new Predicate<CalendarEvent>() {
				  public boolean apply(CalendarEvent event) {
					  
					  return userIds.contains(event.getCrmUserId());
				  }
				};
			return Lists.newArrayList(Maps.filterValues(idMappingTable, filters).values());
	    }
	    
	    public static String formatValue(String formatter, String value) {
	        String res = value;
	        try {
	            if (formatter != null) {
	                Class clazz = Class.forName(formatter);
	                IFormatter formatterImplement = (IFormatter) clazz.newInstance();
	                res = formatterImplement.format(value);
	            }
	        } catch (Exception e) {
	            logger.error("failed to format value",e);
	        }
	        return res;

	    }
	   
	
	    public static EnumSet<Permissions> getPermissionForEntity(int roleId, String entityName) {

	        EnumSet<Permissions> permission = null;
	        if (entityName.equalsIgnoreCase("account")||entityName.equalsIgnoreCase("crmuser")||entityName.equalsIgnoreCase("data_exchange_teample")) {
	            if (roleId == 1) {
	                permission = EnumSet.of(CRUDPanel.Permissions.DEL,CRUDPanel.Permissions.EDIT);
	            }
	        }else if(entityName.equalsIgnoreCase("contact") || entityName.equalsIgnoreCase("calendar")){
	        	 if(roleId==1){
	        		 permission = EnumSet.of(CRUDPanel.Permissions.EDIT,CRUDPanel.Permissions.DEL);
	        	 }else{
	        		 permission = EnumSet.of(CRUDPanel.Permissions.EDIT);
	        	 }
	        }else if(entityName.equalsIgnoreCase("activity")){
	        	if (roleId == 3) {
		            permission = EnumSet.of(CRUDPanel.Permissions.EDIT,CRUDPanel.Permissions.DONE);
	        	}else if(roleId == 1){
	        	  permission = EnumSet.of(CRUDPanel.Permissions.DEL);
	        	}
	        }else if(entityName.equalsIgnoreCase("coaching")){
	          if (roleId == 2){
	            permission = EnumSet.of(CRUDPanel.Permissions.EDIT,CRUDPanel.Permissions.DONE);
	          }else if(roleId == 1){
	            permission = EnumSet.of(CRUDPanel.Permissions.DEL);
	          }
	        }else if(entityName.equalsIgnoreCase("userInfo")){
	            if (roleId == 1) {
	                permission = EnumSet.of(CRUDPanel.Permissions.DEL,CRUDPanel.Permissions.EDIT,CRUDPanel.Permissions.RESETPWD);
	            }
	        }
	        
	        return permission;
	    }
	    
	    
    public static EnumSet<Permissions> getPermissionOfEntityList(int roleId, String entityName) {

        EnumSet<Permissions> permission = null;
        if (entityName.equalsIgnoreCase("account")) {
            if (roleId == 1) {
                permission = EnumSet.of(CRUDPanel.Permissions.ADD,CRUDPanel.Permissions.DOWNLOAD);
            }
        }else if(entityName.equalsIgnoreCase("contact") || entityName.equalsIgnoreCase("calendar")){
        	permission = EnumSet.of(CRUDPanel.Permissions.ADD);
        }else if(entityName.equalsIgnoreCase("activity")){
        	if(roleId == 3){
        		permission = EnumSet.of(CRUDPanel.Permissions.ADD);
        	}else{
        		permission = null;
        	}
        }else if(entityName.equalsIgnoreCase("coaching")){
          if(roleId==1){
        	  permission = EnumSet.of(CRUDPanel.Permissions.DOWNLOAD);
          }else if(roleId ==2){
        	  permission = EnumSet.of(CRUDPanel.Permissions.ADD,CRUDPanel.Permissions.DOWNLOAD);
          }
        }else if(entityName.equalsIgnoreCase("crmuser")||entityName.equalsIgnoreCase("userInfo")||entityName.equalsIgnoreCase("data_exchange_teample")){
            
            if (roleId == 1) {
                permission = EnumSet.of(CRUDPanel.Permissions.ADD);
            }
        }else if(entityName.equalsIgnoreCase("upLoad")){
          
          if (roleId == 1) {
              permission = EnumSet.of(CRUDPanel.Permissions.DOWNLOAD);
          }
      }
        return permission;
    }
	    public static String MD5Base64(String src) {
	        if (src == null || src.isEmpty())
	            return null;

	        java.security.MessageDigest digest = null;
	        try {
	            digest = java.security.MessageDigest.getInstance("MD5");
	        } catch (Throwable ex) {
	            logger.fatal("Exception", ex);
	        }
	        if (null == digest) {
	            logger.fatal("No MD5 digest");
	            return src;
	        }
	        // Convert
	        digest.update(src.getBytes());
	        final byte[] in = digest.digest();
	        if (in == null || in.length <= 0) {
	            logger.fatal("MD5 returned empty digest");
	            return src;
	        }

	        // encode with Base64
	        final String hash = new String(Base64.encodeBase64(in));

	        return hash;
	    }
	    
	    
	    public static String getToolTipById(String id) {
	        Properties systemPeroperties = new Properties();
	          try {
	                systemPeroperties.load(new InputStreamReader(CRMUtility.class.getResourceAsStream("/tooltipMessage.properties"),"UTF-8"));
	            } catch (FileNotFoundException e1) {
	                logger.error(e1);
	            } catch (IOException e1) {
	                logger.error(e1);
	            }
	          String message="";
	          try {
	              message =  String.valueOf(systemPeroperties.getProperty(id));
	           } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	          }
	        return message;
	    }
	    
	   
	    public static void printStat(String type, LogObj object,Type typeOfSrc){
	        
	        UUID uuid  =  UUID.randomUUID(); 
	        object.setUuid(uuid.toString());
	        Gson gson = new Gson();   
            String js = gson.toJson(object, typeOfSrc);
	        logger.info("STAT."+type +"="+js);
	    }

	    public static String readFileAttribure(String fieldName){
			 Properties systemPeroperties = new Properties();
	     	 try {
				systemPeroperties.load(NewDataFormPanel.class.getResourceAsStream("/crm.properties"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     	return systemPeroperties.getProperty(fieldName);
		}
	    
}
