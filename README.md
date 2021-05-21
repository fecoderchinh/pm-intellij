# Packaging Management System
This repo is working perfectly with IntelliJ

#Installation
1. clone source
2. Press `Ctrl + Shift + Alt + S`
    - At `Project` tab, config your `Project compiler output`
    - At `Libraries` tab, press `+` and add `javafx-sdk` version
    - Then press `Apply > OK`
3. Choose `Run > Edit Configurations...` and paste `--module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.fxml` into `VM options` input
4. Press `Apply > OK`
5. Enjoy!

# More details
- You can find the SQL example at `Project > lib > sql.sql`
