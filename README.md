# Student Attendance Mobile Application

A professional, modern Android application for managing student attendance in educational institutions. Built with Java and following Material Design principles, this app provides role-based access control for students, teachers, and administrators.

## 🚀 Features

### Core Functionality
- **User Authentication**: Secure login/registration with OAuth2 support
- **Role-Based Access Control**: Different interfaces for Students, Teachers, and Administrators
- **Attendance Management**: Mark, view, and manage attendance records
- **Class Management**: Create, edit, and manage academic classes
- **Reporting System**: Generate comprehensive attendance reports
- **Real-time Dashboard**: Overview of attendance statistics and upcoming classes

### User Roles & Permissions

#### 👨‍🎓 Students
- View personal attendance history
- Access class schedules and information
- View personal attendance reports
- Update profile information
- Cannot mark attendance or manage other users

#### 👨‍🏫 Teachers
- Mark attendance for their classes
- View class attendance reports
- Manage class information
- View student lists and statistics
- Cannot manage other teachers or system settings

#### 👨‍💼 Administrators
- Full system access and management
- User management (CRUD operations)
- Class and attendance management
- Generate comprehensive reports
- System configuration and monitoring

## 🛠 Tech Stack

### Frontend
- **Language**: Java
- **UI Framework**: Android SDK with Material Design 3
- **Architecture**: MVVM with Repository pattern
- **Navigation**: Android Navigation Component
- **UI Components**: RecyclerView, CardView, BottomNavigationView

### Backend Integration
- **API**: RESTful API with Retrofit2
- **Authentication**: OAuth2 with JWT tokens
- **Data Format**: JSON with Gson serialization
- **Network**: OkHttp with logging and interceptors

### Dependencies
- **Retrofit2**: HTTP client for API communication
- **Glide**: Image loading and caching
- **MPAndroidChart**: Charts and data visualization
- **Room**: Local database (if needed for offline support)
- **Material Design**: UI components and theming

## 📱 Screens & UI

### Welcome & Authentication
- **Welcome Screen**: Modern gradient background with login/register options
- **Login Screen**: Professional form design with validation
- **Registration Screen**: User account creation with role selection

### Main Application
- **Dashboard**: Statistics cards, upcoming classes, and quick actions
- **Attendance Management**: Table/list view for marking attendance
- **Class Management**: List and detail views for academic classes
- **Reports**: Charts and tables for attendance analytics
- **Profile & Settings**: User information and app preferences

### Design Principles
- **Material Design 3**: Modern, accessible UI components
- **Responsive Layout**: Adapts to different screen sizes
- **Professional Color Scheme**: Educational institution branding
- **Smooth Animations**: Enhanced user experience
- **Accessibility**: Support for different user needs

## 🔧 Setup & Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (API level 24)
- Java 11 or later
- Gradle 7.0+

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/student-attendance-app.git
   cd student-attendance-app
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Open the project folder
   - Wait for Gradle sync to complete

3. **Configure API Endpoints**
   - Update `ApiClient.java` with your backend API URL
   - Configure authentication endpoints
   - Set up proper SSL certificates

4. **Build and Run**
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio
   - Install the APK on your device

### Configuration

#### API Configuration
```java
// Update in ApiClient.java
private static final String BASE_URL = "https://your-api-domain.com/api/";
```

#### Authentication Setup
```java
// Configure OAuth2 endpoints in ApiService.java
@POST("auth/login")
Call<AuthResponse> login(@Body LoginRequest loginRequest);
```

## 📊 API Documentation

### Authentication Endpoints

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}
```

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "string",
  "email": "string",
  "password": "string",
  "firstName": "string",
  "lastName": "string",
  "role": "STUDENT|TEACHER|ADMIN"
}
```

### User Management

#### Get User Profile
```http
GET /api/users/profile
Authorization: Bearer {token}
```

#### Update Profile
```http
PUT /api/users/profile
Authorization: Bearer {token}
Content-Type: application/json

{
  "firstName": "string",
  "lastName": "string",
  "email": "string"
}
```

### Attendance Management

#### Get Attendance Records
```http
GET /api/attendance?classId={classId}&date={date}
Authorization: Bearer {token}
```

#### Mark Attendance
```http
POST /api/attendance
Authorization: Bearer {token}
Content-Type: application/json

{
  "studentId": "string",
  "classId": "string",
  "date": "string",
  "status": "PRESENT|ABSENT|LATE|EXCUSED|HALF_DAY"
}
```

### Class Management

#### Get Classes
```http
GET /api/classes?teacherId={teacherId}
Authorization: Bearer {token}
```

#### Create Class
```http
POST /api/classes
Authorization: Bearer {token}
Content-Type: application/json

{
  "className": "string",
  "subject": "string",
  "teacherId": "string",
  "schedule": "string"
}
```

### Reports

#### Generate Attendance Report
```http
GET /api/reports/attendance?classId={classId}&startDate={startDate}&endDate={endDate}
Authorization: Bearer {token}
```

## 🎨 Customization

### Colors & Themes
- **Primary Colors**: Update `colors.xml` for brand colors
- **Material Theme**: Modify `themes.xml` for custom styling
- **Dark Mode**: Configure night theme in `values-night/themes.xml`

### UI Components
- **Custom Views**: Create reusable UI components
- **Animations**: Add custom animations and transitions
- **Icons**: Replace default icons with custom designs

### Localization
- **Strings**: Update `strings.xml` for different languages
- **RTL Support**: Configure right-to-left language support
- **Cultural Adaptations**: Adjust date formats and number systems

## 🔒 Security Features

### Authentication & Authorization
- **JWT Tokens**: Secure token-based authentication
- **Role-Based Access**: Granular permission system
- **Session Management**: Secure session handling
- **Token Refresh**: Automatic token renewal

### Data Protection
- **HTTPS**: Secure communication with backend
- **Input Validation**: Client-side and server-side validation
- **SQL Injection Prevention**: Parameterized queries
- **XSS Protection**: Sanitized input handling

### Privacy Compliance
- **GDPR Compliance**: Data protection regulations
- **FERPA Compliance**: Educational privacy standards
- **Data Encryption**: At-rest and in-transit encryption
- **Audit Logging**: Comprehensive activity tracking

## 📱 Device Support

### Minimum Requirements
- **Android Version**: API level 24 (Android 7.0)
- **RAM**: 2GB minimum, 4GB recommended
- **Storage**: 100MB available space
- **Screen**: 320dp minimum width

### Supported Devices
- **Phones**: All Android phones 7.0+
- **Tablets**: Optimized for 7" and 10" tablets
- **Foldables**: Adaptive layout for foldable devices
- **TV**: Android TV support (if needed)

## 🚀 Deployment

### Production Build
1. **Generate Signed APK**
   ```bash
   ./gradlew assembleRelease
   ```

2. **Google Play Store**
   - Create developer account
   - Upload signed APK
   - Configure store listing
   - Submit for review

3. **Enterprise Distribution**
   - Generate enterprise APK
   - Configure MDM policies
   - Distribute via enterprise channels

### Testing
- **Unit Tests**: JUnit tests for business logic
- **Integration Tests**: API endpoint testing
- **UI Tests**: Espresso tests for user interface
- **Performance Tests**: Memory and battery optimization

## 🤝 Contributing

### Development Guidelines
1. **Code Style**: Follow Java coding conventions
2. **Architecture**: Maintain MVVM pattern
3. **Testing**: Write tests for new features
4. **Documentation**: Update relevant documentation

### Pull Request Process
1. Fork the repository
2. Create feature branch
3. Implement changes with tests
4. Submit pull request with description

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

### Documentation
- **API Reference**: Complete endpoint documentation
- **User Guide**: Step-by-step usage instructions
- **Developer Guide**: Technical implementation details

### Contact
- **Issues**: GitHub Issues for bug reports
- **Discussions**: GitHub Discussions for questions
- **Email**: support@studentattendance.com

### Community
- **Forum**: Community support forum
- **Slack**: Developer community channel
- **Blog**: Latest updates and tutorials

## 🔮 Future Enhancements

### Planned Features
- **Offline Support**: Local database for offline usage
- **Push Notifications**: Real-time attendance alerts
- **Biometric Authentication**: Fingerprint/Face ID login
- **QR Code Scanning**: Quick attendance marking
- **Analytics Dashboard**: Advanced reporting features

### Technology Upgrades
- **Kotlin Migration**: Convert to Kotlin for modern development
- **Jetpack Compose**: Modern declarative UI framework
- **Coroutines**: Asynchronous programming improvements
- **Hilt**: Dependency injection framework

---

**Built with ❤️ for educational institutions worldwide**
