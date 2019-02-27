package securityverification;

import static org.osate.xtext.aadl2.properties.util.GetProperties.*;
import static org.osate.xtext.aadl2.properties.util.InstanceModelUtil.*;
import static securityverification.Av2API.*;

import java.util.List;

import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.InstanceObject;

public class SecurityExposureUtil {
	
	public static boolean isExposed(InstanceObject io) {
		return getBooleanProperty(io,"SecurityProperities::exposed",true);
	}
	
	public static boolean isAuthenticator(InstanceObject io) {
		return getBooleanProperty(io,"SecurityEnforcementProperties::Authenticator",false);
	}
	
	// This method checks whether a component instance (virtual bus) uses encryption (has the property encrypted set to true)
	// If the component instance has a connection binding property then the method check whether all target elements in the binding use encryption.
	public static boolean usesEncryption(ComponentInstance io) {
		if (isEncrypted(io)) return true;
		List<ComponentInstance> vblist = getConnectionBinding(io);
		if (vblist.isEmpty()) return false; //io is not encrypted and there are no lower layer vb
		return forAll(vblist, vb -> usesEncryption(vb));
	}
	

	public static boolean isEncrypted(InstanceObject io) {
		return getBooleanProperty(io,"SecurityEnforcementProperities::Encryption", false);
	}

	public static boolean hasEncryptionScheme(InstanceObject io) {
		return hasAssignedPropertyValue(io,"SecurityProperities::EncryptionScheme");
	}
	//----------------------------------
	//-- Security trust/space boundary related functions
	//----------------------------------

	public static boolean runsOnOS(InstanceObject io, String os ) {
		  return getStringProperty(io, "SecurityEnforcementProperties::OS","none").equalsIgnoreCase(os);
	}
//	 	runs_on_no_virtual_memory_os(p : process) <=
//		  ** p " runs on " os " that does not virtual memory (VM)"**
//		  let os : string = property(p, SecurityEnforcementProperties::OS,"none");
//		  member(os ,Userdefined_Resolute_Constants::Userdefined_Resolute_Constants_public::Resolute::Resolute::NoVirtualMemoryOSes)
//	 
//	  
//		runs_on_partition_enforced_memory_os(p : process) <=
//		  ** p " runs on " os " that  enforces partition level memory access"**
//		  let os : string = property(p, SecurityEnforcementProperties::OS,"none");
//		  member(os ,Userdefined_Resolute_Constants::Userdefined_Resolute_Constants_public::Resolute::Resolute::VirtualMemoryOSes)
//
//	  
//		memory_safe_language_thread(t : thread) <=
//		  ** "thread " t " is generated from " lang **
//		  let lang : string = property(t, SecurityEnforcementProperties::Language);
//		  member(lang ,MemorySafeLanguages)

	
//	----------------------------------
//	-- encryption protection on features, components, and connections
//	----------------------------------

	public static boolean allComponentPortsHaveEncryption(ComponentInstance ci) {
		return forAll(ci.getAllFeatureInstances(), fi -> isEncrypted(fi) || hasEncryptionScheme(fi));
	}
	
	public static boolean allContainedDataHaveEncryption(ComponentInstance root) {
		return forAll(getAllData(root), di -> isEncrypted(di) || hasEncryptionScheme(di));
	}
	
}
