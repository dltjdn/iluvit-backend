node('I_LOVE_IT') {
    def SCM_VARS
    stage('Git Clone') {
        SCM_VARS =
                git(
                branch: 'develop',
                credentialsId: 'e7fe12eb-4666-4cd0-af62-5d18b1c55756',
                url: 'git@github.com:FISOLUTION/ILUVIT_BACK.git'
                )
    }
    stage('has Changed?') {
        script {
            if(currentBuild.changeSets.size() <= 0) {
                sh exit
            }
            echo currentBuild.changeSets.size()
        }
    }
}