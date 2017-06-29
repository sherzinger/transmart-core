package smartr.misc

import grails.core.GrailsApplication
import grails.util.Holders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SmartRRuntimeConstants {
    private static SmartRRuntimeConstants instance;

    public static SmartRRuntimeConstants getInstance() {
        if (instance == null) {
            synchronized (SmartRRuntimeConstants.class) {
                if (instance == null) {
                    instance = new SmartRRuntimeConstants();
                    instance.grailsApplication = Holders.grailsApplication; // thanks to http://stackoverflow.com/a/24501325/535203 (Holders.applicationContext.getBean("smartRRuntimeConstants") and Holders.applicationContext.getBean(SmartRRuntimeConstants) were failing with "org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'smartRRuntimeConstants' is defined"
                }
            }
        }
        instance;
    }

    @Autowired
    private GrailsApplication grailsApplication

    File pluginScriptDirectory
    File remoteScriptDirectoryDir
    File baseDir

    void setPluginScriptDirectory(File dir) {
        assert dir.isDirectory()
        this.pluginScriptDirectory = dir.absoluteFile
    }

    void setRemoteScriptDirectoryDir(Object dir) {
        this.remoteScriptDirectoryDir = dir as File
        getRemoteScriptDirectoryDir() // for the side effects
    }

    /**
     * Where to copy the R scripts to and execute them from
     * (in the machine where Rserve is running).
     */
    File getRemoteScriptDirectoryDir() {
        def dir = this.remoteScriptDirectoryDir
        if (!dir.absolute) {
            throw new RuntimeException("Invalid configuration: " +
                    "smartr.remoteScriptDirectory should be an absolute path," +
                    " got '$dir'")
        }
        dir
    }

    File getBaseDir() {
        def dir = this.baseDir
        if (!dir) {
            dir = File.createTempDir("smartr", ".baseDir");
            setBaseDir(dir)
        }
        dir as File
    }

    void setBaseDir(Object dir) {
        this.baseDir = dir as File
        getBaseDir() // for the side effects
    }
}
