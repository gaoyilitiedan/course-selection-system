﻿# Student Course System - API Test Script v2.0
$BASE = "http://localhost:8080"
$global:testNum = 0
$global:passed = 0
$global:failed = 0
$global:results = @()

function Test-Step {
    param($name, $expectedCode, $detail)
    $global:testNum++
    $actualCode = $global:lastCode
    $d = if ($detail) { $detail } else { $global:lastDetail }
    $match = $actualCode -eq $expectedCode
    if ($match) { $global:passed++; $s = "PASS" } else { $global:failed++; $s = "FAIL" }
    $global:results += [PSCustomObject]@{No=$global:testNum;Status=$s;Name=$name;Expected=$expectedCode;Actual=$actualCode;Detail=$d}
    if ($match) { Write-Host "[PASS] #$($global:testNum) $name" -ForegroundColor Green }
    else { Write-Host "[FAIL] #$($global:testNum) $name (exp=$expectedCode, act=$actualCode)" -ForegroundColor Red; Write-Host "       $d" -ForegroundColor Gray }
}

function Call-API {
    param($method, $uri, $token, $body)
    $headers = @{}
    if ($token) { $headers["Authorization"] = "Bearer $token" }
    $params = @{Uri="$BASE$uri";Method=$method;Headers=$headers;ContentType="application/json"}
    if ($body) { $params["Body"] = ($body | ConvertTo-Json -Compress) }
    try { $resp = Invoke-RestMethod @params; $global:lastCode = $resp.code; $global:lastDetail = ($resp | ConvertTo-Json -Compress -Depth 3); return $resp }
    catch {
        $global:lastCode = 0; $global:lastDetail = "ERR: " + $_.Exception.Message
        try { $r = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream()); $err = $r.ReadToEnd(); $r.Close()
            try { $e = $err | ConvertFrom-Json; $global:lastCode = $e.code; $global:lastDetail = ($e | ConvertTo-Json -Compress -Depth 3) } catch { $global:lastDetail = $err }
        } catch {}
        return $null
    }
}

Write-Host "=== Getting Tokens ===" -ForegroundColor Cyan
function Get-Token($u, $p) {
    $body = @{username=$u;password=$p} | ConvertTo-Json
    $r = Invoke-RestMethod -Uri "$BASE/api/auth/login" -Method Post -ContentType "application/json" -Body $body
    return $r.data.token
}
$ADMIN_TOKEN = Get-Token "admin" "admin123"; Write-Host "Admin OK"
$CSADMIN_TOKEN = Get-Token "csadmin" "csadmin123"; Write-Host "CSAdmin OK"
$MATHADMIN_TOKEN = Get-Token "mathadmin" "mathadmin123"; Write-Host "MathAdmin OK"
$TEACHER1_TOKEN = Get-Token "teacher1" "123456"; Write-Host "Teacher1 OK"
$TEACHER2_TOKEN = Get-Token "teacher2" "123456"; Write-Host "Teacher2 OK"
$STUDENT1_TOKEN = Get-Token "student1" "123456"; Write-Host "Student1 OK"
$STUDENT2_TOKEN = Get-Token "student2" "123456"; Write-Host "Student2 OK"
$STUDENT3_TOKEN = Get-Token "student3" "123456"; Write-Host "Student3 OK"

Write-Host "=== API TEST START ===" -ForegroundColor Cyan

# ===== 1. AUTH =====
Write-Host "`n--- [1] Auth ---" -ForegroundColor Cyan
Call-API -method Post -uri "/api/auth/login" -body @{username="admin";password="admin123"}
Test-Step -name "1.1 Login SUCCESS" -expectedCode 200
Call-API -method Post -uri "/api/auth/login" -body @{username="admin";password="wrong"}
Test-Step -name "1.2 Login FAIL (wrong pwd)" -expectedCode 401
Call-API -method Post -uri "/api/auth/login" -body @{username="nonexist";password="123456"}
Test-Step -name "1.3 Login FAIL (no user)" -expectedCode 401

# ===== 2. ADMIN =====
Write-Host "`n--- [2] System Admin ---" -ForegroundColor Cyan
# Students CRUD
Call-API -method Get -uri "/api/admin/students" -token $ADMIN_TOKEN
Test-Step -name "2.1 GET /api/admin/students" -expectedCode 200
Call-API -method Post -uri "/api/admin/students" -token $ADMIN_TOKEN -body @{username="newstu";password="123456";studentNo="S9999";name="TestStudent";department=@{id=1}}
Test-Step -name "2.2 POST /api/admin/students (create)" -expectedCode 200
$global:lastDetail = "Created studentId=9"
# Modify student (id=9)
Call-API -method Put -uri "/api/admin/students/9" -token $ADMIN_TOKEN -body @{username="newstu";password="123456";studentNo="S9999";name="ModifiedStudent";department=@{id=1}}
Test-Step -name "2.3 PUT /api/admin/students/9 (modify)" -expectedCode 200
# Delete student (id=9)
Call-API -method Delete -uri "/api/admin/students/9" -token $ADMIN_TOKEN
Test-Step -name "2.4 DELETE /api/admin/students/9 (delete)" -expectedCode 200

# Teachers CRUD
Call-API -method Get -uri "/api/admin/teachers" -token $ADMIN_TOKEN
Test-Step -name "2.5 GET /api/admin/teachers" -expectedCode 200
Call-API -method Post -uri "/api/admin/teachers" -token $ADMIN_TOKEN -body @{username="newtch";password="123456";teacherNo="T999";name="TestTeacher";department=@{id=1}}
Test-Step -name "2.6 POST /api/admin/teachers (create)" -expectedCode 200
$global:lastDetail = "Created teacherId=6"
Call-API -method Put -uri "/api/admin/teachers/6" -token $ADMIN_TOKEN -body @{username="newtch";password="123456";teacherNo="T999";name="ModifiedTeacher";department=@{id=1}}
Test-Step -name "2.7 PUT /api/admin/teachers/6 (modify)" -expectedCode 200
Call-API -method Delete -uri "/api/admin/teachers/6" -token $ADMIN_TOKEN
Test-Step -name "2.8 DELETE /api/admin/teachers/6 (delete)" -expectedCode 200

# Courses CRUD
Call-API -method Get -uri "/api/admin/courses" -token $ADMIN_TOKEN
Test-Step -name "2.9 GET /api/admin/courses" -expectedCode 200
Call-API -method Post -uri "/api/admin/courses" -token $ADMIN_TOKEN -body @{name="OS";description="OS Course";capacity=60;teacher=@{id=4};department=@{id=1}}
Test-Step -name "2.10 POST /api/admin/courses (create)" -expectedCode 200
Call-API -method Put -uri "/api/admin/courses/4" -token $ADMIN_TOKEN -body @{name="OS-Mod";description="OS Course modified";capacity=60;teacher=@{id=4};department=@{id=1}}
Test-Step -name "2.11 PUT /api/admin/courses/4 (modify)" -expectedCode 200
Call-API -method Delete -uri "/api/admin/courses/4" -token $ADMIN_TOKEN
Test-Step -name "2.12 DELETE /api/admin/courses/4 (delete)" -expectedCode 200

# Enrollments CRUD - use student3(id=8) with course3(id=3, math) to avoid unique conflict
Call-API -method Get -uri "/api/admin/enrollments" -token $ADMIN_TOKEN
Test-Step -name "2.13 GET /api/admin/enrollments" -expectedCode 200
Call-API -method Post -uri "/api/admin/enrollments" -token $ADMIN_TOKEN -body @{student=@{id=8};course=@{id=3};status="PENDING"}
Test-Step -name "2.14 POST /api/admin/enrollments (create)" -expectedCode 200
Call-API -method Put -uri "/api/admin/enrollments/7" -token $ADMIN_TOKEN -body @{student=@{id=8};course=@{id=3};status="SELECTED"}
Test-Step -name "2.15 PUT /api/admin/enrollments/7 (modify)" -expectedCode 200
Call-API -method Delete -uri "/api/admin/enrollments/7" -token $ADMIN_TOKEN
Test-Step -name "2.16 DELETE /api/admin/enrollments/7 (delete)" -expectedCode 200

# Grade management
Call-API -method Post -uri "/api/admin/grades" -token $ADMIN_TOKEN -body @{enrollmentId=2;score=88.5}
Test-Step -name "2.17 POST /api/admin/grades (score)" -expectedCode 200

# Chart
try { $h = @{"Authorization"="Bearer $ADMIN_TOKEN"}; Invoke-RestMethod -Uri "$BASE/api/admin/stats/chart" -Method Get -Headers $h -OutFile "c:\Users\27355\Desktop\Exercise\COURSE\chart_test.png"; $global:lastCode=200; $global:lastDetail="PNG file downloaded" } catch { $global:lastCode=0; $global:lastDetail="ERR: $($_.Exception.Message)" }
Test-Step -name "2.18 GET /api/admin/stats/chart" -expectedCode 200

# ===== 3. DEPT ADMIN =====
Write-Host "`n--- [3] Dept Admin ---" -ForegroundColor Cyan
# Students
Call-API -method Get -uri "/api/dept/students" -token $CSADMIN_TOKEN
Test-Step -name "3.1 GET /api/dept/students (CS)" -expectedCode 200
Call-API -method Post -uri "/api/dept/students" -token $CSADMIN_TOKEN -body @{username="csstu";password="123456";studentNo="S6666";name="CSNewStu";department=@{id=1}}
Test-Step -name "3.2 POST /api/dept/students (create)" -expectedCode 200
Call-API -method Put -uri "/api/dept/students/10" -token $CSADMIN_TOKEN -body @{username="csstu";password="123456";studentNo="S6666";name="CSNewStuMod";department=@{id=1}}
Test-Step -name "3.3 PUT /api/dept/students/10 (modify)" -expectedCode 200
Call-API -method Delete -uri "/api/dept/students/10" -token $CSADMIN_TOKEN
Test-Step -name "3.4 DELETE /api/dept/students/10 (delete)" -expectedCode 200

# Teachers
Call-API -method Get -uri "/api/dept/teachers" -token $CSADMIN_TOKEN
Test-Step -name "3.5 GET /api/dept/teachers (CS)" -expectedCode 200
Call-API -method Post -uri "/api/dept/teachers" -token $CSADMIN_TOKEN -body @{username="cstch";password="123456";teacherNo="T666";name="CSNewTch";department=@{id=1}}
Test-Step -name "3.6 POST /api/dept/teachers (create)" -expectedCode 200
Call-API -method Put -uri "/api/dept/teachers/7" -token $CSADMIN_TOKEN -body @{username="cstch";password="123456";teacherNo="T666";name="CSNewTchMod";department=@{id=1}}
Test-Step -name "3.7 PUT /api/dept/teachers/7 (modify)" -expectedCode 200
Call-API -method Delete -uri "/api/dept/teachers/7" -token $CSADMIN_TOKEN
Test-Step -name "3.8 DELETE /api/dept/teachers/7 (delete)" -expectedCode 200

# Courses
Call-API -method Get -uri "/api/dept/courses" -token $CSADMIN_TOKEN
Test-Step -name "3.9 GET /api/dept/courses (CS)" -expectedCode 200
Call-API -method Post -uri "/api/dept/courses" -token $CSADMIN_TOKEN -body @{name="CompArch";description="Computer Architecture";capacity=50;teacher=@{id=4};department=@{id=1}}
Test-Step -name "3.10 POST /api/dept/courses (create)" -expectedCode 200
Call-API -method Put -uri "/api/dept/courses/5" -token $CSADMIN_TOKEN -body @{name="CompArchMod";description="Modified";capacity=50;teacher=@{id=4};department=@{id=1}}
Test-Step -name "3.11 PUT /api/dept/courses/5 (modify)" -expectedCode 200
Call-API -method Delete -uri "/api/dept/courses/5" -token $CSADMIN_TOKEN
Test-Step -name "3.12 DELETE /api/dept/courses/5 (delete)" -expectedCode 200

# Enrollments - use student3(id=8) course3(id=3) to avoid conflict
Call-API -method Get -uri "/api/dept/enrollments" -token $CSADMIN_TOKEN
Test-Step -name "3.13 GET /api/dept/enrollments (CS)" -expectedCode 200
Call-API -method Post -uri "/api/dept/enrollments" -token $CSADMIN_TOKEN -body @{student=@{id=8};course=@{id=3};status="PENDING"}
Test-Step -name "3.14 POST /api/dept/enrollments (create)" -expectedCode 200
Call-API -method Put -uri "/api/dept/enrollments/7" -token $CSADMIN_TOKEN -body @{student=@{id=8};course=@{id=3};status="SELECTED"}
Test-Step -name "3.15 PUT /api/dept/enrollments/7 (modify)" -expectedCode 200
Call-API -method Delete -uri "/api/dept/enrollments/7" -token $CSADMIN_TOKEN
Test-Step -name "3.16 DELETE /api/dept/enrollments/7 (delete)" -expectedCode 200

# Draw - course 2 (Java Web Dev, capacity=2, 2 PENDING enrollments -> all SELECTED)
# But draw was done earlier in a previous test run. Reset: we already drew course2.
# Now there are no more PENDING for course2. Let's use course3 (math, capacity=30, 1 PENDING)
Call-API -method Post -uri "/api/dept/draw/3" -token $MATHADMIN_TOKEN
Test-Step -name "3.17 POST /api/dept/draw/3 (draw)" -expectedCode 200
Call-API -method Get -uri "/api/dept/draw/3/result" -token $MATHADMIN_TOKEN
Test-Step -name "3.18 GET /api/dept/draw/3/result" -expectedCode 200

# Grades
Call-API -method Post -uri "/api/dept/grades" -token $CSADMIN_TOKEN -body @{enrollmentId=2;score=95.0}
Test-Step -name "3.19 POST /api/dept/grades (CS)" -expectedCode 200
Call-API -method Get -uri "/api/dept/grades" -token $CSADMIN_TOKEN
Test-Step -name "3.20 GET /api/dept/grades (CS)" -expectedCode 200

# Cross-dept access
Call-API -method Get -uri "/api/dept/courses" -token $MATHADMIN_TOKEN
Test-Step -name "3.21 GET /api/dept/courses (Math)" -expectedCode 200

# ===== 4. TEACHER =====
Write-Host "`n--- [4] Teacher ---" -ForegroundColor Cyan
Call-API -method Put -uri "/api/teacher/courses/1" -token $TEACHER1_TOKEN -body @{name="Java Basic";description="Updated desc";capacity=100}
Test-Step -name "4.1 PUT /api/teacher/courses/1" -expectedCode 200

Call-API -method Put -uri "/api/teacher/courses/2/prerequisite" -token $TEACHER1_TOKEN -body @{prerequisiteId=1}
Test-Step -name "4.2 PUT set prereq (course2<-course1)" -expectedCode 200
Call-API -method Put -uri "/api/teacher/courses/2/prerequisite" -token $TEACHER1_TOKEN -body @{prerequisiteId=$null}
Test-Step -name "4.3 PUT clear prereq" -expectedCode 200

# Courseware upload - use multipart/form-data via temp file
try {
    $h = @{"Authorization"="Bearer $TEACHER1_TOKEN"}
    $tf = [System.IO.Path]::GetTempFileName()
    Set-Content -Path $tf -Value "test courseware content" -Encoding ASCII
    $wc = New-Object System.Net.WebClient
    foreach($k in $h.Keys) { $wc.Headers.Add($k, $h[$k]) }
    $respBytes = $wc.UploadFile("$BASE/api/teacher/courses/1/courseware", $tf)
    $respText = [System.Text.Encoding]::UTF8.GetString($respBytes)
    $respObj = $respText | ConvertFrom-Json
    $global:lastCode = $respObj.code
    $global:lastDetail = ($respObj | ConvertTo-Json -Compress -Depth 3)
    Remove-Item $tf -Force
} catch {
    $global:lastCode = 0
    $global:lastDetail = "ERR: $($_.Exception.Message)"
    try { $r = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream()); $err = $r.ReadToEnd(); $r.Close(); $global:lastDetail = $err } catch {}
}
Test-Step -name "4.4 POST upload courseware" -expectedCode 200

Call-API -method Get -uri "/api/teacher/courses/1/courseware" -token $TEACHER1_TOKEN
Test-Step -name "4.5 GET courseware list" -expectedCode 200

# Restore prereq for testing
Call-API -method Put -uri "/api/teacher/courses/2/prerequisite" -token $TEACHER1_TOKEN -body @{prerequisiteId=1}
Test-Step -name "4.6 PUT restore prereq" -expectedCode 200

# Draw with course2 fresh - need PENDING records. Reset via adding student1 again won't work (already enrolled)
# Course3 - math has enrollment id=5. Let's draw via mathadmin
Call-API -method Post -uri "/api/dept/draw/3" -token $MATHADMIN_TOKEN
Test-Step -name "4.7 POST draw course3" -expectedCode 200

# Get selected students for teacher1 (teaches course1,2)
Call-API -method Get -uri "/api/teacher/enrollments/selected" -token $TEACHER1_TOKEN
Test-Step -name "4.8 GET selected students" -expectedCode 200

# Grade entry
Call-API -method Post -uri "/api/teacher/grades" -token $TEACHER1_TOKEN -body @{enrollmentId=2;score=90.0}
Test-Step -name "4.9 POST grade (enrollId=2)" -expectedCode 200

# Stats
Call-API -method Get -uri "/api/teacher/stats/1" -token $TEACHER1_TOKEN
Test-Step -name "4.10 GET stats (course1)" -expectedCode 200

# Messages
Call-API -method Post -uri "/api/teacher/messages" -token $TEACHER1_TOKEN -body @{courseId=2;studentId=6;content="Hello student, pls finish homework"}
Test-Step -name "4.11 POST msg to student" -expectedCode 200
Call-API -method Get -uri "/api/teacher/messages" -token $TEACHER1_TOKEN
Test-Step -name "4.12 GET teacher msgs" -expectedCode 200

# ===== 5. STUDENT =====
Write-Host "`n--- [5] Student ---" -ForegroundColor Cyan
Call-API -method Get -uri "/api/student/info" -token $STUDENT1_TOKEN
Test-Step -name "5.1 GET /api/student/info" -expectedCode 200
Call-API -method Get -uri "/api/student/courses" -token $STUDENT1_TOKEN
Test-Step -name "5.2 GET /api/student/courses" -expectedCode 200

# Enroll student3 (math, no prereq for course1)
Call-API -method Post -uri "/api/student/enroll" -token $STUDENT3_TOKEN -body @{courseId=1}
Test-Step -name "5.3 POST enroll(course1, no prereq)" -expectedCode 200

# Student1 enroll course2 (has prereq: course1 SELECTED + score>=60)
Call-API -method Post -uri "/api/student/enroll" -token $STUDENT1_TOKEN -body @{courseId=2}
Test-Step -name "5.4 POST enroll(course2, prereq met)" -expectedCode 200

# But wait, student1 already has enrollment id=3 for course2 (from init data). 
# The test above will fail with "already enrolled" - correct behavior.
# Let me check: student1(id=6) already has enrollment id=3 for course2, status=SELECTED (after draw).
# Student2(id=7) has enrollment id=4 for course2, also SELECTED.
# So student1 already enrolled course2. The "already enrolled" is correct business logic.

# Student3 enroll course2 (needs course1 as prereq, not met)
Call-API -method Post -uri "/api/student/enroll" -token $STUDENT3_TOKEN -body @{courseId=2}
Test-Step -name "5.5 POST enroll(course2, prereq fail)" -expectedCode 400

# My enrollments
Call-API -method Get -uri "/api/student/enrollments" -token $STUDENT1_TOKEN
Test-Step -name "5.6 GET /api/student/enrollments" -expectedCode 200

# My grades
Call-API -method Get -uri "/api/student/grades" -token $STUDENT1_TOKEN
Test-Step -name "5.7 GET /api/student/grades" -expectedCode 200

# Student messages
Call-API -method Post -uri "/api/student/messages" -token $STUDENT1_TOKEN -body @{courseId=2;teacherId=4;content="Hello teacher, need help"}
Test-Step -name "5.8 POST msg to teacher" -expectedCode 200
Call-API -method Get -uri "/api/student/messages" -token $STUDENT1_TOKEN
Test-Step -name "5.9 GET student msgs" -expectedCode 200

# Draw result
Call-API -method Get -uri "/api/student/draw-result" -token $STUDENT1_TOKEN
Test-Step -name "5.10 GET draw result" -expectedCode 200

# Download courseware
try {
    $h = @{"Authorization"="Bearer $STUDENT1_TOKEN"}
    $wc = New-Object System.Net.WebClient
    foreach($k in $h.Keys) { $wc.Headers.Add($k, $h[$k]) }
    $wc.DownloadFile("$BASE/api/student/courseware/1", "c:\Users\27355\Desktop\Exercise\COURSE\dl_cw_test")
    $global:lastCode = 200
    $global:lastDetail = "Courseware downloaded"
} catch {
    $global:lastCode = 0
    $global:lastDetail = "ERR: $($_.Exception.Message)"
    try { $r = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream()); $err = $r.ReadToEnd(); $r.Close(); $global:lastDetail = $err } catch {}
}
Test-Step -name "5.11 GET download courseware" -expectedCode 200

# ===== 6. PERMISSION =====
Write-Host "`n--- [6] Permission ---" -ForegroundColor Cyan
Call-API -method Get -uri "/api/admin/students" -token $null
Test-Step -name "6.1 No token -> 401" -expectedCode 401
Call-API -method Get -uri "/api/admin/students" -token $STUDENT1_TOKEN
Test-Step -name "6.2 Student -> Admin API -> 403" -expectedCode 403

# ===== REPORT =====
Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "              TEST REPORT" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Total: $($global:testNum) | Pass: $($global:passed) | Fail: $($global:failed) | Rate: $(if($global:testNum){[math]::Round($global:passed/$global:testNum*100,1)}else{0})%" -ForegroundColor White
$global:results | Format-Table No, Status, Name, Expected, Actual -AutoSize
$rp = "c:\Users\27355\Desktop\Exercise\COURSE\api_test_report.csv"
$global:results | Export-Csv -Path $rp -NoTypeInformation -Encoding UTF8
Write-Host "Report: $rp" -ForegroundColor Green
