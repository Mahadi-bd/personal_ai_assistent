# GitHub + Google Play Store guide (step by step)

> Ei guide Banglish e lekha. Ekta ekta step follow koro.

## 0. Age ei 3 ta jinis fill koro
| Kothay | Ki likhbe |
|---|---|
| `docs/privacy-policy.md` ar `docs/privacy-policy.html` | `[YOUR EMAIL ADDRESS]` ar `[DATE]` er jaygay tomar email ar ajker tarikh |
| `store_assets/how_to_use_guide.md` ar `store_assets/listing_en.md` | `[YOUR EMAIL ADDRESS]` |
| `app/src/main/java/com/example/ui/screens/AboutSection.kt` | `PRIVACY_POLICY_URL = ""` er moddhe tomar privacy policy er link (step 5 e pabe) |

**Package name:** `com.lifeeasy.app` (file: `app/build.gradle.kts`, `applicationId`). Play Store e prothombar upload korar por eta r change kora jay na. Onno kono name chaile ekhoni change koro.

## 1. GitHub e upload
1. https://github.com e login → **New repository** → name: `life-easy` → **Create** (README/gitignore add korba na).
2. Computer e zip extract kore folder e terminal kholo:
```bash
git init
git add .
git commit -m "LIFE EASY v1.0.0"
git branch -M main
git remote add origin https://github.com/TOMAR_USERNAME/life-easy.git
git push -u origin main
```
(Terminal na thakle: GitHub repo page → **uploading an existing file** → extract kora folder er shob file drag kore dao, `.github` folder ta shoho.)

`.env`, `*.jks` file kokhono upload hobe na (`.gitignore` e block kora ache). Tomar API key ar keystore GitHub e dio na.

## 2. Gemini API key (AI chat er jonno)
1. https://aistudio.google.com/apikey theke key nao.
2. Google Cloud Console → APIs & Services → Credentials → tomar key → **Application restrictions → Android apps** → add:
   - package: `com.lifeeasy.app`
   - SHA-1: tomar **upload key** er SHA-1 ar (Play upload er pore) Play Console → App integrity → **App signing key certificate** er SHA-1.
   App ei restriction er jonno `X-Android-Package` / `X-Android-Cert` header pathay.
3. **API restrictions** e shudhu *Generative Language API* rakho.
> Key APK er bhitore thake, tai restriction chara key ber kore neya jay. Restriction deya must.
> Model name `app/src/main/java/com/example/data/ai/GeminiAiService.kt` e `GeminiModels` e ache. Google kono model bondho korle ekhane id change koro.

## 3. Signing key (upload key) banano
Ekbar bananor por backup rakho (pen drive/Google Drive private). Hariye felle Play Console e "upload key reset" request korte hoy.
```bash
keytool -genkeypair -v -keystore my-upload-key.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000
```
Password diye details dao. Tarpor base64 text banao:
- Linux/Mac: `base64 -w0 my-upload-key.jks` (Mac: `base64 -i my-upload-key.jks`)
- Windows PowerShell: `[Convert]::ToBase64String([IO.File]::ReadAllBytes("my-upload-key.jks"))`

## 4. GitHub Secrets
Repo → **Settings → Secrets and variables → Actions → New repository secret**:
| Name | Value |
|---|---|
| `GEMINI_API_KEY` | tomar Gemini key |
| `KEYSTORE_BASE64` | step 3 er base64 text |
| `STORE_PASSWORD` | keystore password |
| `KEY_ALIAS` | `upload` |
| `KEY_PASSWORD` | key password |

## 5. Privacy policy link (GitHub Pages)
Repo → **Settings → Pages** → Source: *Deploy from a branch* → Branch `main`, folder `/docs` → Save.
Link hobe: `https://TOMAR_USERNAME.github.io/life-easy/privacy-policy.html`
Ei link ta Play Console e dibe ar `AboutSection.kt` e boshabe.

## 6. APK / AAB build (GitHub Actions)
- **Test APK:** repo → **Actions → Build test APK → Run workflow**. Shesh hole artifact `LIFE-EASY-debug-apk` download kore phone e install koro ar test koro. Ei APK diyei screenshot nao.
- **Play Store AAB:** **Actions → Build signed AAB for Google Play → Run workflow** → `version_code` dao (prothombar `1`, protibar update e baraiba). Shesh hole artifact `LIFE-EASY-release-aab` download koro (zip er bhitore `.aab`).

Build fail korle Actions log er red step ta amake pathao, ami thik kore dibo.

## 7. Google Play Console
1. https://play.google.com/console → developer account (ekbar fee, identity verification lagbe).
2. **Create app** → name `LIFE EASY – AI Assistant`, language, App, Free.
3. **App content** section e shob form fill koro: `store_assets/data_safety_answers.md` ar `store_assets/content_rating_and_forms.md` dekhe. Privacy policy link dao.
4. **Store listing:** text `store_assets/listing_en.md` (ar Bangla `listing_bn.md` — Add translation), icon `store_assets/graphics/play_store_icon_512.png`, feature graphic `store_assets/graphics/feature_graphic_1024x500.png`, phone screenshots (`store_assets/screenshots/README.md`).
5. **Testing → Closed testing** (ba Production) → Create release → `.aab` upload → Play App Signing **accept** koro → release notes → Review → Rollout.
6. Notun personal developer account hole Google production e jawar age closed test (kichu tester, kichu din) chay — Play Console e ki lagbe ta ekhon dekhe nio, rule bodole jay.
7. Review e kichu din lage.

## 8. Update dile
`versionName` (`app/build.gradle.kts`) barao, workflow te `version_code` age er cheye boro dao, notun AAB upload koro.
