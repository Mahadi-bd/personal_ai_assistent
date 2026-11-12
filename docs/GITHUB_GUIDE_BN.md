# GitHub গাইড (বাংলা)

## ১. GitHub এ project তোলা (একবারই)
1. github.com এ লগইন → ডান উপরে **+ → New repository** → নাম `life-easy` → **Public** সিলেক্ট (Check for update ও সহজ download এর জন্য) → **Create repository**।
2. zip ফাইল (`LIFE-EASY-android-project.zip`) extract করো।
3. repo পেজে **uploading an existing file** লিংকে চাপো → extract করা ফোল্ডারের **সব ফাইল ও ফোল্ডার** (`.github` ও `ci` ফোল্ডারসহ) drag করে ছাড়ো → নিচে **Commit changes**।
   - `.github` ফোল্ডার না গেলে git দিয়ে দাও: `git init && git add . && git commit -m first && git branch -M main && git remote add origin https://github.com/USERNAME/life-easy.git && git push -u origin main`

## ২. একবার সেটিং (১ মিনিট)
**Settings → Actions → General → Workflow permissions → Read and write permissions → Save।**

## ৩. APK বানানো (২-৩ ক্লিক)
**Actions → Build APK and publish release → Run workflow → Run workflow।**
৫-১০ মিনিট পর সবুজ ✓ আসবে।

## ৪. APK download
Repo পেজের ডানে **Releases → সর্বশেষ release → LIFE-EASY.apk** চাপো।
সবসময়ের সরাসরি লিংক: `https://github.com/USERNAME/life-easy/releases/latest/download/LIFE-EASY.apk`
ফোনে install করার সময় "Install unknown apps" অনুমতি দাও।

## ৫. Update দেওয়া
কোড বদলে GitHub এ upload করলেই (বা আবার Run workflow দিলে) নতুন version নিজে নিজে তৈরি হয়। ফোনে নতুন APK install করলে পুরনোটার উপরেই update হয়। অ্যাপের ভিতরে **Persona ট্যাব → Check for update** চাপলেও নতুন version থাকলে download খুলবে।

## ৬. AI চালু করতে
**Settings → Secrets and variables → Actions → New repository secret** → নাম `GEMINI_API_KEY`, value তোমার Gemini key।

## ৭. Play Store এর জন্য (পরে)
`GITHUB_AND_PLAY_STORE_GUIDE.md` এ আসল upload key বানানো ও secrets (`KEYSTORE_BASE64`, `STORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`) দেওয়ার নিয়ম আছে। ওই secrets দিলে একই workflow `LIFE-EASY.aab` ও বানাবে।
**সতর্কতা:** ci ফোল্ডারের test key শুধু GitHub APK এর জন্য, Play Store এ ব্যবহার করো না। Play Store এর AAB এ "Check for update" বাটন থাকে না (Play এর নিয়মের জন্য)।
