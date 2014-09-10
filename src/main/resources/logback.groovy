appender("stdout", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
	pattern = "%d [%thread] [%level] %logger - %msg%n"
  }
}

logger("org.springframework.batch", INFO)
//logger("org.springframework.batch", DEBUG)
//logger("org.springframework.jdbc", DEBUG)
//logger("org.springframework", INFO)
logger("example", INFO)
root(WARN, ["stdout"])