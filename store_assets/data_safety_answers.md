# Play Console → App content → Data safety (suggested answers)

Always double-check against the final build. These answers match the code in this repository.

**Does your app collect or share any of the required user data types?** Yes (user content is sent to a service provider to run a feature).

**Is all of the user data collected by your app encrypted in transit?** Yes (all calls use HTTPS).

**Do you provide a way for users to request that their data is deleted?** The app has no accounts and keeps no server-side user data. Choose the option that says data is processed ephemerally / users can delete data by uninstalling (read the wording in the form carefully).

| Data type (Play Console wording) | Collected? | Shared? | Purpose | Notes |
|---|---|---|---|---|
| Messages → Other in-app messages (text typed/spoken into AI chat) | Yes | Yes – to Google (Gemini API), a service provider | App functionality | Processed to produce the answer. Mark "processed ephemerally" only if you are sure; otherwise leave it unchecked. |
| Audio → Voice or sound recordings | The app does not record. The phone's speech service (Google) transcribes. | – | App functionality | Declare as collected & shared with Google if you want to be conservative; Google's own policy applies. |
| Approximate location | No (only the city name typed by the user is sent to Open-Meteo) | – | – | City name is user-entered text, not device location. |
| Personal info, financial info, contacts, photos, files, device IDs, ads ID | No | No | – | Income/expense entries stay on the device. |
| Advertising / analytics | No SDKs | – | – | – |

**Permissions to explain (if asked):** RECORD_AUDIO – used only when the user taps the microphone button for voice input.
