# Packaging Management System
This repo is working perfectly with **IntelliJ IDEA**

#Installation
1. Clone source
2. Press `Ctrl + Shift + Alt + S`
    - At `Project` tab, config your `Project compiler output`
    - At `Libraries` tab, press `+` and add `javafx-sdk` version
    - Then press `Apply > OK`
3. Choose `Run > Edit Configurations...`
4. Paste the below code into `VM options`
   
   `--module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.fxml`
5. Press `Apply > OK`
6. Enjoy!

# More details
- You can find the SQL example at `Project > lib > sql.sql`
