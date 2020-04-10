Firebase restrictions to consider:
We limit collapsible messages to a burst of 20 messages per app per device, with a refill of 1 message every 3 minutes.
You can send up to 240 messages/minute and 5,000 messages/hour to a single device
The topic subscription add/remove rate is limited to 3,000 QPS per project.

export GOOGLE_APPLICATION_CREDENTIALS="/home/user/Downloads/service-account-file.json"