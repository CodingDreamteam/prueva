package org.map.zk.loginController;


import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.map.zk.SystemConstans.SystemConstans;
import org.map.zk.database.CDatabaseConnection;
import org.map.zk.database.CDatabaseConnectionConfig;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import commonlibs.commonclasses.CLanguage;
import commonlibs.commonclasses.ConstantsCommonClasses;
import commonlibs.extendedlogger.CExtendedLogger;
import commonlibs.utils.Utilities;
import commonlibs.utils.ZKUtilities;

public class  CloginController  extends SelectorComposer<Component>  {

	private static final long serialVersionUID = 3211193732865097784L;

	public static final String Databasekey = "database";  
    protected Execution execution = Executions.getCurrent();
    protected CDatabaseConnection ConnectionDatabase = null;
    protected CExtendedLogger controllogger=null;
    protected CLanguage controllanguaje=null;
   
    @Wire
    Textbox Tuser;

    @Wire
    Textbox Tpassword;
    
    @Wire
    Label LMessage;
    
    
    @Override
	public void doAfterCompose(Component comp) {
        try {
            super.doAfterCompose(comp);
            controllogger = (CExtendedLogger) Sessions.getCurrent().getWebApp().getAttribute(SystemConstans._Webapp_Logger_App_Attribute_Key);

        }catch (Exception e) {
            if(controllogger!=null){
            	controllogger.logException("-1021", e.getMessage(),e);
            	
            }
        }

    }
    

    
    //onChangin es una accion que nos permite que cuando seleccionemos un textbox lo limpie si tiene algo dentro
	@Listen("onChanging=#Tuser ;onChanging=#Tpassword")
    public void onChangeTextBox (Event event){
		
		
		if(event.getTarget().equals(Tuser)){//de esta forma podemos distinguir de cual de los dos eventos se selecciono
			System.out.println("Text Box usuario");//puede ser util deacuerdo a lo que se quiera hacer 
		}else if(event.getTarget().equals(Tpassword)){// en mi opinion esto ayuda mucho a algo que me gusta hacer que es 
			System.out.println("Text Box password");//utilizar el codigo de un metodo para varias cosas soloc ambiando pequiños detalles
		}											
		
		LMessage.setValue("");
	
	}


	@Listen("onClick=#BLogin")
    public void onClickBlogin (Event event){
		try{
			//aqui nos conectamos a laDB y verificamos que el user exista y la password sea correcta
			final String username= ZKUtilities.getTextBoxValue(Tuser, controllogger);
			final String userpassword= ZKUtilities.getTextBoxValue(Tpassword, controllogger);
			Session session = Sessions.getCurrent();
			if( username.isEmpty()== false && userpassword.isEmpty() ==false ){
				ConnectionDatabase = new CDatabaseConnection(); 
				CDatabaseConnectionConfig config=new CDatabaseConnectionConfig();
				
				String patch = Sessions.getCurrent().getWebApp().getRealPath(SystemConstans._WEB_INF_DIR) + File.separator+ SystemConstans._CONFIG_DIR +File.separator;
				if( config.LoadConfig(patch+SystemConstans._DATABASE_CONFIG_FILE,controllogger, controllanguaje)){
	 
			     /*   if(ConnectionDatabase.makeConnectionToDatabase(config,controllogger,controllanguaje)){//Si logra conectarse  
					 	TBLUser operador = UserDAO.checkvalid(ConnectionDatabase, username, userpassword, controllogger, controllanguaje);
					 		
					 	if(operador!=null){
					 			
					 	Session currentSession = Sessions.getCurrent();
					 			
					 			
					 	LMessage.setSclass("");
					 			
					 	LMessage.setValue("Welcome "+operador.getName()+"!");
					 			
					 	session.setAttribute(Databasekey, ConnectionDatabase);

					 	session.setAttribute(SystemConstans._Operator_Credential_Session_Key, operador);

					 	controllogger.logMessage( "1" , CLanguage.translateIf( controllanguaje, "Saved user credential in session for user [%s]", operador.getName() ) );

		                //Obtenemos la fecha y la hora en el formato yyyy-MM-dd-HH-mm-ss
		                String DateTime = Utilities.getDateInFormat( ConstantsCommonClasses._Global_Date_Time_Format_File_System_24, null );
		                            
		                //Creamos la variable del logpath
		                String LogPath = patch + SystemConstans._LOGS_DIR + username + File.separator + DateTime + File.separator;
		                            
		                //La guardamos en la sesion
		                currentSession.setAttribute( SystemConstans._Log_Path_Session_Key, LogPath );

		                controllogger.logMessage( "1" , CLanguage.translateIf( controllanguaje, "Saved user log path [%s] in session for user [%s]", LogPath, username ) );
		                            
		                //Guardamos la fecha y la hora del inicio de sesión
		                currentSession.setAttribute( SystemConstans._Login_Date_Time_Session_Key, DateTime );
		                            
		                controllogger.logMessage( "1" , CLanguage.translateIf( controllanguaje, "Saved user login date time [%s] in session for user [%s]", DateTime, username ) );
		                            
		                //Creamos la lista de logger de esta sesion
		                List<String> loggedSessionLoggers = new LinkedList<String>();
		                            
		                //Guardamos la lista vacia en la sesion
		                currentSession.setAttribute( SystemConstans._Logged_Session_Loggers, loggedSessionLoggers );
		                            
		                //Actualizamos en bd el último inicio de sesión
		                UserDAO.updateLastLogin( ConnectionDatabase, operador.getId(), controllogger, controllanguaje );                            
		                            
		                //Redirecionamos hacia el home.zul
		                Executions.sendRedirect( "/views/home/home.zul" ); 
					 		
					 }else{
					    LMessage.setValue("error en el usuaro o password");
					 			
					 }
			      }else{
			           
			         Messagebox.show("Conexion fallida!.");
			          
			    }*/
		         
		      }else{
		    	  
				 Messagebox.show("Error al leer el archivo de configuracion");
		    
		      }
	       
			}
		}catch (Exception e) {
            if(controllogger!=null){
            	
            	controllogger.logException("-1021", e.getMessage(),e);
            	
            }
		}
		
	}
	
	
    @Listen("onTimer=#TimerSession")
    public void onTimer(Event event){
    	//debido a la definicion del timer en el .zul del index este evento se ejecuta cada 60000 milisegudos 1 minuto
    	//muestra en tablero con un mensaje en la parte superior centralde la pantalla
    	//esto se hace para evitar que caduquen las sessiones y de esa forma dar un mejor servicio
    	Clients.showNotification("automatic session successful", "info", null, "before_center", 2000);;
    	
    }
	
	
	
}

