package smartr

import org.springframework.beans.factory.annotation.Autowired
import smartr.session.SessionService

class SmartrController {

    @Autowired
    SessionService sessionService

    def index() {
        [scriptList: sessionService.availableWorkflows()]
    }

    def loadScripts() {

    }

    def smartRContextPath() {
        render servletContext.contextPath + pluginContextPath as String
    }
}
