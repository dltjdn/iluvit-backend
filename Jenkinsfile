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
        def CHANGE
        script {
            CHANGE = java.lang.String.valueOf(currentBuild.changeSets.size())
            if(CHANGE.equals('0')) {
                currentBuild.result = 'SUCCESS'
                sh "exit 1"
            }
        }
        echo CHANGE
    }
    stage('cd') {
        sh "who"
        sh "whoami"
        sh "pwd"
        sh "su -"
        sh "cd /root"
        sh "ls"
    }
}