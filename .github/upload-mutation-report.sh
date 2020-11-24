#!/usr/bin/env bash

# Copyright 2020 Maarten Mulders
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -Euo pipefail

trap "rm mutation-testing-report.json" ERR

BASE_URL="https://dashboard.stryker-mutator.io"
PROJECT="github.com/${GITHUB_REPOSITORY}"
VERSION=${GITHUB_REF#refs/heads/}

reportJsLocations=$(find . -name "report.js")
echo Found report.js files at ${reportJsLocations}

for reportJsLocation in ${reportJsLocations}; do
  echo Processing ${reportJsLocation}
  reportJsContent=$(<${reportJsLocation})
  report="${reportJsContent:60}"
  echo "${report}" > mutation-testing-report.json

  module=$(echo ${reportJsLocation} | sed -e "s|\./\(.*\)/target/pit-reports/html2/report.js|\1|g")
  echo Module is ${module}

  echo "Uploading mutation-testing-report.json to ${BASE_URL}/api/reports/${PROJECT}/${VERSION}?module=${module}"
  curl -X PUT \
    "${BASE_URL}/api/reports/${PROJECT}/${VERSION}?module=${module}" \
    -H "Content-Type: application/json" \
    -H "Host: dashboard.stryker-mutator.io" \
    -H "X-Api-Key: ${API_KEY}" \
    -d @mutation-testing-report.json

  rm mutation-testing-report.json

  echo

done
