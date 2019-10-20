#!/bin/sh
set -e

if ! command -v python3 > /dev/null; then
    echo "python3 executable not found - please install Python 3"
    exit 1
fi

if ! python3 -c 'import sys; sys.exit(sys.version_info < (3, 6))'; then
    echo "python3 executable is too old - Python 3.6 or higher is required"
    exit 1
fi

if ! command -v aws > /dev/null; then
    echo "aws executable not found - please install awscli"
    exit 1
fi

# aws configure help will throw up a man page by default, so try and pipe in "q" for the pager
if ! echo "q" | aws configure help 2>/dev/null | grep -q "add-model"; then
    echo "aws executable is too old - please install the latest version of awscli"
    exit 1
fi

if ! command -v mvn > /dev/null; then
    echo "mvn executable not found - please install Maven"
    exit 1
fi

echo "*** pre-checks completed"

aws configure add-model \
    --service-model "file://cloudformation-2010-05-15.normal.json" \
    --service-name cloudformation

echo "*** SDK patched"

python3 -m venv env

echo "*** virtual environment created under env/"

env/bin/pip install --upgrade pip setuptools wheel > /dev/null
env/bin/pip install aws_cloudformation_rpdk-*-py3-none-any.whl aws_cloudformation_rpdk_java_plugin-*-py3-none-any.whl > /dev/null

echo "*** RPDK components installed into virtual environment"

SCHEMA_JAR=( aws-cloudformation-resource-schema-*.jar )
PLUGIN_JAR=( aws-cloudformation-rpdk-java-plugin-*.jar )
mvn org.apache.maven.plugins:maven-install-plugin:3.0.0-M1:install-file --batch-mode -Dfile="${SCHEMA_JAR[0]}" > /dev/null
mvn org.apache.maven.plugins:maven-install-plugin:3.0.0-M1:install-file --batch-mode -Dfile="${PLUGIN_JAR[0]}" > /dev/null

echo "*** Java support libraries installed"

ABSPATH="$PWD/env/bin/cfn-cli"

if ! command -v "$ABSPATH" > /dev/null; then
    echo "Installation failed (binary not found inside virtual env)"
    exit 1
fi

echo "*** RPDK executable location verified"

if ! env/bin/cfn-cli --help | grep -q "CloudFormation"; then
    echo "Installation failed:"
    set +e
    env/bin/cfn-cli --help
    exit 1
fi

echo "*** RPDK executable quicktested"

echo "COMPLETE"
echo "The CFN CLI was installed to \"$ABSPATH\""
echo "Please run the following command to temporarily make the CFN CLI available from any folder:"
echo "source $PWD/env/bin/activate"
echo "(The above command is required for the Maven build process to work)"
