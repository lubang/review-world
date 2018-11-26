# Review World

## Development Environment

### Test configuration

Review world service's unittest need an authentication to interface external services (Github, Gerrit, Slack, LineWorks). You have two options to configure properties for testing.

1. File: properties.test.env 
2. Environment 

```
GITHUB_USERNAME=your github username
GITHUB_PASSWORD=your github password
SLACK_WEBHOOK=your slack webhook URL
SLACK_CHANNEL=your slack channel
```

Review world unittest find a properties.test.env file at first. And get environment variables when the file is not exist.