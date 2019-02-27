package securityverification;

import static org.osate.result.util.ResultUtil.createResult;
import static org.osate.xtext.aadl2.properties.util.InstanceModelUtil.getConnectionBinding;
import static org.osate.xtext.aadl2.properties.util.InstanceModelUtil.isVirtualBus;
import static securityverification.Av2API.addFailure;
import static securityverification.Av2API.exists;
import static securityverification.Av2API.forAll;
import static securityverification.SecurityExposureUtil.isExposed;
import static securityverification.SecurityExposureUtil.usesEncryption;

import java.util.List;

import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.ConnectionInstance;
import org.osate.result.Result;

public class SecurityExposureVerification {
// this is an example of a Resolute method rewritten in Java.
// The method uses Result as return parameter. This allows it to return a collection of issues.
	// In our case we return an issue for every connection instance owned by a component.
	
	// the method checks all connection instances owned by a component if they use encryption when bound to an exposed component.
	// It reports overall result and failure messages from individual tests
	public static Result allExposedConnectionsEncrypted(ComponentInstance ci) {
		Result result = createResult("For all connections: use encryption if connection is bound to exposed hardware",ci);
		for (ConnectionInstance conni : ci.getConnectionInstances()) {
			if (!checkBoundVirtualBusExposureEncrypted(conni)) {
				addFailure(result,"Connection " + conni.getName() + " with exposed bound component is not encrypted",
						conni);
			}
			if (!checkDirectComponentBindingExposureEncrypted(conni)) {
				addFailure(result,"Connection " + conni.getName() + " with exposed directly bound component is not encrypted",
						conni);
			}
		}
		return result;
	}

	// This is an example of a verification method that returned true if pass, or false if failed.
	// The verification message to be shown is contributed by ALisa
	public static boolean exposedConnectionEncrypted(ConnectionInstance conni) {
		return checkBoundVirtualBusExposureEncrypted(conni) && checkDirectComponentBindingExposureEncrypted(conni);
	}

	// connection is bound to virtual buses, which in turn as at some point bound to a physical component.
	// If the physical component is exposed then we need at least one layer of encryption
	public static boolean checkBoundVirtualBusExposureEncrypted(ConnectionInstance conni) {
		List<ComponentInstance> vblist = getConnectionBinding(conni);
		return forAll(vblist, vb ->(usesEncryption(vb) || !usesExposedComponent(vb)));
	}

	// connection is bound to virtual bus(es) with encryption) at a given layer of virtual buses
	// this method focuses on determining whether all virtual buses in a layer have encryption set
	public static boolean checkBoundVirtualBusEncrypted(ConnectionInstance conni) {
		List<ComponentInstance> vblist = getConnectionBinding(conni);
		return forAll(vblist, vb ->!usesEncryption(vb) );
	}
	
	// This method handles the case where a connection is directly bound to a (set of) physical component(s)
	// if any physical component is exposed then the binding must also include virtual buses with encryption
	public static boolean checkDirectComponentBindingExposureEncrypted(ConnectionInstance conni) {
		List<ComponentInstance> vblist = getConnectionBinding(conni);
		if( checkBoundVirtualBusEncrypted(conni)) return true;
		return forAll(vblist, vb->!isVirtualBus(vb) && isExposed(vb) );
	}
	
	
	
	// the component instance has the property exposed set to true.
	public static boolean usesExposedComponent(ComponentInstance ci) {
		if (!isVirtualBus(ci) && isExposed(ci)) return true;
		List<ComponentInstance> vblist = getConnectionBinding(ci);
		return exists(vblist, vb -> usesExposedComponent(vb));
	}


}
