import requests
import json
import os

print("Updating pull request labels")

GITHUB_RELEASE_API_TOKEN = os.environ.get('GITHUB_RELEASE_API_TOKEN')
JIRA_AUTH = os.environ.get('JIRA_AUTH')

# Get all OPEN PR
opened_prs = requests.get("https://api.github.com/repos/dhis2/dhis2-android-capture-app/pulls?state=open&access_token=%s" %(GITHUB_RELEASE_API_TOKEN)).json()
for pr in opened_prs:
    print(pr['number'])
    labels_to_add = []
    has_reviews = requests.get("https://api.github.com/repos/dhis2/dhis2-android-capture-app/pulls/%s/reviews?access_token=%s" %(pr['number'], GITHUB_RELEASE_API_TOKEN)).json()

    #Select label for PR type
    labelType = ""
    if pr['title'].find("build") == 0:
        labelType = "enhancement"
    elif pr['title'].find('ci') == 0:
        labelType = "enhancement"
    elif pr['title'].find('docs') == 0:
        labelType = "Documentation"
    elif pr['title'].find('feat') == 0:
        labelType = "enhancement"
    elif pr['title'].find('fix') == 0:
        labelType = "bug"
    elif pr['title'].find('perf') == 0:
        labelType = "refactor"
    elif pr['title'].find('refactor') == 0:
        labelType = "refactor"
    elif pr['title'].find('style') == 0:
        labelType = "design"
    elif pr['title'].find('test') == 0:
        labelType = "Unit Test"
    else:
        labelType = ""

    if labelType != "":
        labels_to_add.append(labelType)
        
    print(str(labels_to_add)[1:-1])
    payload = '{"labels":%s}' % json.dumps(labels_to_add)
    requests.put("https://api.github.com/repos/dhis2/dhis2-android-capture-app/issues/%s/labels?access_token=%s" %(pr['number'], GITHUB_RELEASE_API_TOKEN), data = payload)
print("Finished updating labels")
