import org.mortbay.jetty.webapp.WebAppClassLoader
import org.mortbay.resource.ResourceCollection
import org.gradle.api.plugins.jetty.internal.JettyPluginWebAppContext

class MyWebAppClassLoader extends WebAppClassLoader {
	public MyWebAppClassLoader(JettyPluginWebAppContext context) {
		super(context)
	}
	
	public void addClassPath(String classPath) {
		if (!classPath.startsWith('file:')) {
			super.addClassPath(classPath)
		}
	}
}

class MyJettyPluginWebAppContext extends JettyPluginWebAppContext {
	public void doStart() {
		setClassLoader(new MyWebAppClassLoader(this))
		super.doStart()
	}
	
	public void doStop() {
		try {
			super.doStop()
		} finally {
			setClassLoader(null)
		}
	}
}

def context = new MyJettyPluginWebAppContext()

context.baseResource = new ResourceCollection()

context