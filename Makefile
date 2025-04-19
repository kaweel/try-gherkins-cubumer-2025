start-mock-server:
	@echo "🚀 Starting wiremock standalone"
	java -jar jar-wiremock-standalone/wiremock-standalone-3.12.1.jar \
	--port 9090 \
	--root-dir ./src/test/resources/wiremock