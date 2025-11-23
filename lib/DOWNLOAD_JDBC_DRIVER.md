# Download MySQL Connector/J

You need to download the MySQL JDBC driver and place it in this folder.

## Steps:

1. **Visit**: https://dev.mysql.com/downloads/connector/j/

2. **Select**: 
   - Platform: Platform Independent
   - Download the ZIP file

3. **Extract** the downloaded ZIP file

4. **Find** the JAR file:
   - Look for `mysql-connector-java-x.x.x.jar` or `mysql-connector-j-x.x.x.jar`

5. **Copy** the JAR file to this `lib` folder

6. **Rename** (optional) to `mysql-connector-java-8.0.33.jar` for easier reference

## Alternative Direct Downloads:

### MySQL Connector/J 8.0.33 (Recommended)
https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.0.33.zip

### MySQL Connector/J 8.0.28
https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-8.0.28.zip

## Verification:

After downloading, this folder should contain:
```
lib/
├── mysql-connector-java-8.0.33.jar  (or similar)
└── DOWNLOAD_JDBC_DRIVER.md (this file)
```

## Troubleshooting:

**Issue**: Can't download from MySQL website
**Solution**: 
- You might need to create a free Oracle account
- Or use direct download links above
- Or search "mysql connector java download" in Google

**Issue**: Which version to download?
**Solution**: 
- Any version 8.0.x or higher works
- Recommended: 8.0.33 (latest stable as of 2024)

## For Students:

If you're submitting this project:
- Include the JAR file in your submission ZIP
- Or provide clear download instructions
- Make sure to mention this in your README

**Note**: This driver is required for the application to connect to MySQL database!
