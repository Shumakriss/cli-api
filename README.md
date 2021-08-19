# cli-api
A Java command line tool backed by AWS API Gateway and Lambda

You can deploy with `./deploy.sh`.

Once deployed, the `CommandRunner` main class can discover
the endpoints. Any arguments to `CommandRunner` will be sent to
the `Handler` and dealt with there.

You can add client-side code to `CommandRunner` such as 
preliminary checks to fail fast, perform uploads, etc.

You can add cloud-side code to `Handler` to run processes 
with lots of integrations or long run times. This helps
simplify access for users as they only need permission to
get the SSM parameter. You can also build a lightweight client
with fewer dependencies. This means smaller jars. The same 
approach could be used in Python.