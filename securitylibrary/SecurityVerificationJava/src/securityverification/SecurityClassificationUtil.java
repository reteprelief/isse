package securityverification;

import static securityverification.Av2API.getBooleanProperty;

import org.osate.aadl2.instance.InstanceObject;

public class SecurityClassificationUtil {


	public static boolean isTrusted(InstanceObject io) {
		return getBooleanProperty(io,"SecurityProperities::trusted",false);
	}

}
