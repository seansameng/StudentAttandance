# Class Management System Testing Guide

## 🚨 **Current Issue**
The Class Management system is not working properly. This guide will help you test and debug the system.

## 🔧 **What I've Fixed**

1. **Added missing activities to AndroidManifest.xml**:
   - `AddClassActivity`
   - `EditClassActivity`

2. **Fixed icon references**:
   - Changed add class button from `ic_edit` to `ic_add`
   - Floating action button already uses correct `ic_add` icon

3. **Added database testing functionality**:
   - Changed "Manage Enrollments" button to "Test Database"
   - Added sample class creation for testing

## 📱 **How to Test**

### **Step 1: Access Class Management**
1. Launch the app
2. Login as an admin user (username: `admin`, password: `123456`)
3. Navigate to Admin Panel
4. Select "Class Management" tab

### **Step 2: Test Database Connection**
1. Click the "Test Database" button (formerly "Manage Enrollments")
2. This will:
   - Test if the database is accessible
   - Create sample classes if none exist
   - Show a toast message with the result

### **Step 3: Check for Sample Classes**
If the database test is successful, you should see:
- **Mathematics 101**: Monday, Wednesday, Friday 9:00 AM - 10:30 AM, Room 101
- **Science Fundamentals**: Tuesday, Thursday 2:00 PM - 3:30 PM, Room 102
- **English Literature**: Monday, Wednesday 11:00 AM - 12:30 PM, Room 103

### **Step 4: Test Class Management Features**
1. **Add Class**: Click the "Add New Class" button or floating action button
2. **View Class**: Click on any class in the list and select "View Details"
3. **Edit Class**: Click on any class and select "Edit Class"
4. **Delete Class**: Click on any class and select "Delete Class"

## 🐛 **Debugging Steps**

### **Check Logs**
Look for these log tags in Android Studio:
- `ClassManagementFragment`
- `AdminFragment`
- `DataRepository`

### **Common Issues**

1. **"Access Denied" Message**:
   - Check if you're logged in as admin
   - Verify user role in SessionManager

2. **"No Classes Found"**:
   - Click "Test Database" button
   - Check database connection

3. **Layout Issues**:
   - Verify all drawable resources exist
   - Check if layout files are properly inflated

4. **Database Errors**:
   - Check if Room database is initialized
   - Verify database schema version

## 🔍 **What to Look For**

### **In Logs**:
```
ClassManagementFragment: Creating class management view...
ClassManagementFragment: Views initialized successfully
ClassManagementFragment: RecyclerView setup completed
ClassManagementFragment: Loading classes data...
ClassManagementFragment: Database test - Current classes count: X
```

### **In UI**:
- Class statistics cards showing counts
- Class list with sample data
- Search and filter functionality
- Add/Edit/Delete buttons working

## 🚀 **Expected Behavior**

When working correctly, you should see:
1. **Header**: "Class Management" with description
2. **Statistics Cards**: Total Classes, Active Classes, Total Students counts
3. **Action Buttons**: "Add New Class" and "Test Database"
4. **Search & Filter**: Search bar and filter/sort dropdowns
5. **Class List**: RecyclerView showing classes with options
6. **Floating Action Button**: Quick add class access

## 📋 **Test Checklist**

- [ ] Can access Class Management tab
- [ ] Statistics cards display correctly
- [ ] "Test Database" button works
- [ ] Sample classes are created
- [ ] Class list displays classes
- [ ] Search functionality works
- [ ] Filtering works
- [ ] Sorting works
- [ ] Add class button launches AddClassActivity
- [ ] Edit class functionality works
- [ ] Delete class functionality works
- [ ] Floating action button works

## 🆘 **If Still Not Working**

1. **Check Android Studio Logcat** for error messages
2. **Verify database initialization** in AppDatabase
3. **Check SessionManager** for user authentication
4. **Verify all activities** are declared in manifest
5. **Check drawable resources** exist and are accessible

## 📞 **Support**

If you continue to have issues:
1. Share the logcat output
2. Describe what you see on screen
3. Mention any error messages
4. Specify which step fails

---

**Note**: The system is designed to be fully functional. If it's not working, there's likely a configuration or initialization issue that we can resolve together.
