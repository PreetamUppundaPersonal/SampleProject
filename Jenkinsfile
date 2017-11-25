node('testScor') {

	docker.image("b4crenewals/make").inside("-v /var/run/docker.sock:/var/run/docker.sock") {

        try {

            stage 'Checkout latest from Goggs'
                git url: "http://git.b4c.directlinegroup.co.uk:3000/DLG/SCOR-TestAutomation.git", credentialsId:"git"

            stage 'Clean'
                sh 'mvn clean'

            stage 'Install'
                sh 'mvn install -DskipTests'

            stage 'Run Tests'
                sh 'mvn clean test -P'+ENV+' -Dfeatures="src/test/resources/features/claimportal-features" -Dsurefire.fork.count=5'

            stage 'Test Report'
                step([$class: 'ArtifactArchiver', artifacts: '**/target/cucumber_reports/regression_results/*.json', fingerprint: true])
                sh 'mvn exec:java -Dexec.mainClass="co.uk.directlinegroup.evo.JSONReportMerger" -Dexec.args="target/cucumber_reports/regression_results" -Dexec.classpathScope=test'
                step([$class: 'CucumberReportPublisher', fileIncludePattern: '**/cucumber.json', ignoreFailedTests: false, jenkinsBasePath: '', jsonReportDirectory: 'target', parallelTesting: false, pendingFails: false, skippedFails: false, undefinedFails: false])
                echo 'Test Reports should be produced'

        } catch (e){

            stage 'Test Report'
                step([$class: 'ArtifactArchiver', artifacts: '**/target/cucumber_reports/regression_results/*.json', fingerprint: true])
                sh 'mvn exec:java -Dexec.mainClass="co.uk.directlinegroup.evo.JSONReportMerger" -Dexec.args="target/cucumber_reports/regression_results" -Dexec.classpathScope=test'
                step([$class: 'CucumberReportPublisher', fileIncludePattern: '**/cucumber.json', ignoreFailedTests: false, jenkinsBasePath: '', jsonReportDirectory: 'target', parallelTesting: false, pendingFails: false, skippedFails: false, undefinedFails: false])
                echo 'Test Reports should be produced'

                throw e

        }
    }
}