package securityverification;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.Property;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.ConnectionInstance;
import org.osate.aadl2.instance.InstanceObject;
import org.osate.result.AnalysisResult;
import org.osate.result.Diagnostic;
import org.osate.result.Result;
import org.osate.result.ResultFactory;
import org.osate.result.util.ResultUtil;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.InstanceModelUtil;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

public class SecurityVerification {
// this is an example of a Resolute method rewritten in Java.
// The method uses Result as return parameter. This allows it to return a collection of issues.
	// In our case we return an issue for every connection instance owned by a component.
	
	// the method checks all connection instances owned by a component if they use encryption when bound to an exposed component.
	public static Result allExposedConnectionsEncrypted(ComponentInstance ci) {
		Result report = ResultFactory.eINSTANCE.createResult();
		for (ConnectionInstance conni : ci.getConnectionInstances()) {
			if (!checkBoundVirtualBusExposureEncrypted(conni)) {
				Diagnostic issue = ResultUtil.createFailure("Connection " + conni.getName() + " with exposed bound component is not encrypted",
						conni);
				report.getDiagnostics().add(issue);
			}
			if (!checkDirectComponentBindingExposureEncrypted(conni)) {
				Diagnostic issue = ResultUtil.createFailure("Connection " + conni.getName() + " with exposed directly bound component is not encrypted",
						conni);
				report.getDiagnostics().add(issue);
			}
		}
		return report;
	}

	// This is an example of a verification method that returned true if pass, or false if failed.
	
	// the method calls on two other methods.
	public static boolean exposedConnectionEncrypted(ConnectionInstance conni) {
		return checkBoundVirtualBusExposureEncrypted(conni) && checkDirectComponentBindingExposureEncrypted(conni);
	}

	// connection is bound to virtual buses, which in turn as at some point bound to a physical component.
	// If the physical component is exposed then we need at least one layer of encryption
	public static boolean checkBoundVirtualBusExposureEncrypted(ConnectionInstance conni) {
		List<ComponentInstance> vblist = InstanceModelUtil.getConnectionBinding(conni);
		for (ComponentInstance vb: vblist) {
			if (!(usesEncryption(vb) || !usesExposedComponent(vb))) {
				return false;
			}
		}
		return true;
	}

	// connection is bound to virtual bus(es) with encryption) at a given layer of virtual buses
	// this method focuses on determining whether all virtual buses in a layer have encryption set
	public static boolean checkBoundVirtualBusEncrypted(ConnectionInstance conni) {
		List<ComponentInstance> vblist = InstanceModelUtil.getConnectionBinding(conni);
		for (ComponentInstance vb: vblist) {
			if (!usesEncryption(vb) ) {
				return false;
			}
		}
		return true;
	}
	
	// This method handles the case where a connection is directly bound to a (set of) physical component(s)
	// if any physical component is exposed then the binding must also include virtual buses with encryption
	public static boolean checkDirectComponentBindingExposureEncrypted(ConnectionInstance conni) {
		List<ComponentInstance> vblist = InstanceModelUtil.getConnectionBinding(conni);
		if( checkBoundVirtualBusEncrypted(conni)) return true;
		for (ComponentInstance vb: vblist) {
			if (!InstanceModelUtil.isVirtualBus(vb) && isExposed(vb) ) {
				return false;
			}
		}
		return true;
	}
	
	
	// This method checks whether a component instance (virtual bus) uses encryption (has the property encrypted set to true)
	// If the component instance has a connection binding property then the method check whether all target elements in the binding use encryption.
	public static boolean usesEncryption(ComponentInstance io) {
		if (isEncrypted(io)) return true;
		List<ComponentInstance> vblist = InstanceModelUtil.getConnectionBinding(io);
		if (vblist.isEmpty()) return false; //io is not encrypted and there are no lower layer vb
		for (ComponentInstance vb: vblist) {
			if (!usesEncryption(vb)) {
				return false;
			}
		}
		// all in list are true
		return true;
	}
	
	
	// the component instance has the property exposed set to true.
	public static boolean usesExposedComponent(ComponentInstance ci) {
		if (!InstanceModelUtil.isVirtualBus(ci) && isExposed(ci)) return true;
		List<ComponentInstance> vblist = InstanceModelUtil.getConnectionBinding(ci);
		for (ComponentInstance vb: vblist) {
			if (usesExposedComponent(vb)) {
				return true;
			}
		}
		return false;
	}
	

	static Property EncryptionPD = null;
	public static boolean isEncrypted(InstanceObject io) {
		if (EncryptionPD == null) {
			EncryptionPD = GetProperties.lookupPropertyDefinition(io, "SecurityProperities::encryption");
		}
		return PropertyUtils.getBooleanValue(io, EncryptionPD,false);
	}


	static Property TrustedPD = null;
	public static boolean isTrusted(InstanceObject io) {
		if (TrustedPD == null) {
			TrustedPD = GetProperties.lookupPropertyDefinition(io, "SecurityProperities::trusted");
		}
		return PropertyUtils.getBooleanValue(io, TrustedPD,false);
	}

	static Property ExposedPD = null;
	public static boolean isExposed(InstanceObject io) {
		if (ExposedPD == null) {
			ExposedPD = GetProperties.lookupPropertyDefinition(io, "SecurityProperities::exposed");
		}
		return PropertyUtils.getBooleanValue(io, TrustedPD,false);
	}
	
	// when conditions for requirements
	
	public static boolean isPortConnection(EObject conn) {
		return conn instanceof PortConnection;
	}


}
