package org.map.zk.home;

import java.util.LinkedList;

import org.map.zk.SystemConstans.SystemConstans;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tabbox;

import commonlibs.commonclasses.CLanguage;
import commonlibs.commonclasses.ConstantsCommonClasses;
import commonlibs.extendedlogger.CExtendedConfigLogger;
import commonlibs.extendedlogger.CExtendedLogger;
import commonlibs.utils.Utilities;

public class ChomeController extends SelectorComposer<Component> {

	private static final long serialVersionUID = -6992273830457634170L;

    protected CExtendedLogger controllerLogger = null;
    
    protected CLanguage controllerLanguage = null;
    
    @Wire( "#includeNorthContent #labelHeader" )
    Label labelHeader;
    
    @Wire
    Tabbox tabboxMainContent;
	
    public void initControllerLoggerAndControllerLanguage( String strRunningPath, Session currentSession ) {
        
     
        CExtendedConfigLogger extendedConfigLogger = SystemUtilities.initLoggerConfig( strRunningPath, currentSession );

    
        TBLOperator operatorCredential = (TBLOperator) currentSession.getAttribute( SystemConstans._Operator_Credential_Session_Key );

        
        String Operator = SystemConstans._Operator_Unknown; //Esto es un valor por defecto no debería quedar con el pero si lo hacer el algoritmo no falla
        String LoginDateTime = (String) currentSession.getAttribute( SystemConstans._Login_Date_Time_Session_Key ); //Recuperamos información de fecha y hora del inicio de sesión Login
        String LogPath = (String) currentSession.getAttribute( SystemConstans._Log_Path_Session_Key ); //Recuperamos el path donde se guardarn los log ya que cambia según el nombre de l operador que inicie sesion  

        if ( operatorCredential != null )
            Operator = operatorCredential.getName();  //Obtenemos el nombre del operador que hizo login

        if ( LoginDateTime == null ) //En caso de ser null no ha fecha y hora de inicio de sesión colocarle una por defecto
            LoginDateTime = Utilities.getDateInFormat( ConstantsCommonClasses._Global_Date_Time_Format_File_System_24, null );

        final String LoggerName = SystemConstans._Home_Controller_Logger_Name;
        final String LoggerFileName = SystemConstans._Home_Controller_File_Log;
        
        controllerLogger = CExtendedLogger.getLogger( LoggerName + " " + Operator + " " + LoginDateTime );
       
        if ( controllerLogger.getSetupSet() == false ) {

            if ( LogPath == null )
                LogPath = strRunningPath + "/" + SystemConstans._Logs_Dir;

          
            if ( extendedConfigLogger != null )
                controllerLogger.setupLogger( Operator + " " + LoginDateTime, false, LogPath, LoggerFileName, extendedConfigLogger.getClassNameMethodName(), extendedConfigLogger.getExactMatch(), extendedConfigLogger.getLevel(), extendedConfigLogger.getLogIP(), extendedConfigLogger.getLogPort(), extendedConfigLogger.getHTTPLogURL(), extendedConfigLogger.getHTTPLogUser(), extendedConfigLogger.getHTTPLogPassword(), extendedConfigLogger.getProxyIP(), extendedConfigLogger.getProxyPort(), extendedConfigLogger.getProxyUser(), extendedConfigLogger.getProxyPassword() );
            else   
                controllerLogger.setupLogger( Operator + " " + LoginDateTime, false, LogPath, LoggerFileName, SystemConstans.LOG_CLASS_METHOD, SystemConstans.LOG_EXACT_MATCH, SystemConstans.log_level, "", -1, "", "", "", "", -1, "", "" );

            //Inicializamos el lenguage para ser usado por el logger
            controllerLanguage = CLanguage.getLanguage( controllerLogger, strRunningPath + SystemConstans._Langs_Dir + LoggerName + "." + SystemConstans._Lang_Ext ); 

            //Protección para el multi hebrado, puede que dos usuarios accedan exactamente al mismo tiempo a la página web, este código en el servidor se ejecuta en dos hebras
            synchronized( currentSession ) { 
            
                //Guardamos en la sesisón los logger que se van creando para luego ser destruidos.
                @SuppressWarnings("unchecked")
                LinkedList<String> loggedSessionLoggers = (LinkedList<String>) currentSession.getAttribute( SystemConstans._Logged_Session_Loggers );

                if ( loggedSessionLoggers != null ) {

                    synchronized( loggedSessionLoggers ) {

                        //Lo agregamos a la lista
                        loggedSessionLoggers.add( LoggerName + " " + Operator + " " + LoginDateTime );

                    }

                    //Lo retornamos la sesión de este operador
                    currentSession.setAttribute( SystemConstans._Logged_Session_Loggers, loggedSessionLoggers );

                }
            
            }
            
        }
    
      }
      
      @Listen("onClick= #includeNorthContent #BLogout")
      public void onClicLogout(Event event){
	       System.out.println("Logout");
      }
	
}
