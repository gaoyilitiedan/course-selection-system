﻿# Student Course System - API Test Script
$BASE = "http://localhost:8080"
$global:testNum = 0
$global:passed = 0
$global:failed = 0
$global:results = @()

function Test-Step {
    param($name, $expectedCode)
    $global:testNum++
    $actualCode = $global:lastCode
    $detail = $global:lastDetail
    $match = $actualCode -eq $expectedCode
    if ($match) { $global:passed++; $s = "PASS" } else { $global:failed++; $s = "FAIL" }
    $global:results += [PSCustomObject]@{No=$global:testNum;Status=$s;Name=$name;ExpectedCode=$expectedCode;ActualCode=$actualCode;Detail=$detail}
    if ($match) { Write-Host "[PASS] #$($global:testNum) $name" -ForegroundColor Green }
    else { Write-Host "[FAIL] #$($global:testNum) $name (exp=$expectedCode, act=$actualCode)" -ForegroundColor Red; Write-Host "       $detail" -ForegroundColor Gray }
}

function Call-API {
    param($method, $uri, $token, $body)
    $headers = @{}
    if ($token) { $headers["Authorization"] = "Bearer $token" }
    $params = @{Uri="$BASE$uri";Method=$method;Headers=$headers;ContentType="application/json"}
    if ($body) { $params["Body"] = ($body | ConvertTo-Json -Compress) }
    try { $resp = Invoke-RestMethod @params; $global:lastCode = $resp.code; $global:lastDetail = ($resp | ConvertTo-Json -Compress -Depth 10); return $resp }
    catch { $global:lastCode = 0; $global:lastDetail = "ERR: " + $_.Exception.Message; try { $r = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream()); $global:lastDetail = $r.ReadToEnd(); $r.Close() } catch {}; return $null }
}

Write-Host "=== Getting Tokens ===" -ForegroundColor Cyan
$body = '{"username": "admin", "password": "admin123"}'
$r = Invoke-RestMethod -Uri "$BASE/api/auth/login" -Method Post -ContentType "application/json" -Body $body
$ADMIN_TOKEN = $r.data.token; Write-Host "Admin OK" -ForegroundColor Yellow

$body = '{"username": "csadmin", "password": "csadmin123"}'
$r = Invoke-RestMethod -Uri "$BASE/api/auth/login" -Method Post -ContentType "application/json" -Body $body
$CSADMIN_TOKEN = $r.data.token; Write-Host "CSAdmin OK" -ForegroundColor Yellow

$body = '{"username": "mathadmin", "password": "mathadmin123"}'
$r = Invoke-RestMethod -Uri "$BASE/api/auth/login" -Method Post -ContentType "application/json" -Body $body
$MATHADMIN_TOKEN = $r.data.token; Write-Host "MathAdmin OK" -ForegroundColor Yellow

$body = '{"username": "teacher1", "password": "123456"}'
$r = Invoke-RestMethod -Uri "$BASE/api/auth/login" -Method Post -ContentType "application/json" -Body $body
$TEACHER1_TOKEN = $r.data.token; Write-Host "Teacher1 OK" -ForegroundColor Yellow

$body = '{"username": "teacher2", "password": "123456"}'
$r = Invoke-RestMethod -Uri "$BASE/api/auth/login" -Method Post -ContentType "application/json" -Body $body
$TEACHER2_TOKEN = $r.data.token; Write-Host "Teacher2 OK" -ForegroundColor Yellow

$body = '{"username": "student1", "password": "123456"}'
$r = Invoke-RestMethod -Uri "$BASE/api/auth/login" -Method Post -ContentType "application/json" -Body $body
$STUDENT1_TOKEN = $r.data.token; Write-Host "Student1 OK" -ForegroundColor Yellow

$body = '{"username": "student2", "password": "123456"}'
$r = Invoke-RestMethod -Uri "$BASE/api/auth/login" -Method Post -ContentType "application/json" -Body $body
$STUDENT2_TOKEN = $r.data.token; Write-Host "Student2 OK" -ForegroundColor Yellow

$body = '{"username": "student3", "password": "123456"}'
$r = Invoke-RestMethod -Uri "$BASE/api/auth/login" -Method Post -ContentType "application/json" -Body $body
$STUDENT3_TOKEN = $r.data.token; Write-Host "Student3 OK" -ForegroundColor Yellow

Write-Host "=== API TEST START ===" -ForegroundColor Cyan

# ===== 1. AUTH =====
Write-Host "`n--- [1] Auth ---" -ForegroundColor Cyan
Call-API -method Post -uri "/api/auth/login" -body @{username="admin";password="admin123"}
Test-Step -name "1.1 Login OK(admin)" -expectedCode 200
Call-API -method Post -uri "/api/auth/login" -body @{username="admin";password="wrong"}
Test-Step -name "1.2 Login Fail(wrong pwd)" -expectedCode 401
Call-API -method Post -uri "/api/auth/login" -body @{username="nonexist";password="123456"}
Test-Step -name "1.3 Login Fail(no user)" -expectedCode 401

# ===== 2. ADMIN =====
Write-Host "`n--- [2] Admin ---" -ForegroundColor Cyan
Call-API -method Get -uri "/api/admin/students" -token $ADMIN_TOKEN
Test-Step -name "2.1 GET students list" -expectedCode 200
Call-API -method Post -uri "/api/admin/students" -token $ADMIN_TOKEN -body @{username="newstu";password="123456";studentNo="S9999";name="TestStudent";department=@{id=1}}
Test-Step -name "2.2 POST add student" -expectedCode 200
Call-API -method Put -uri "/api/admin/students/9" -token $ADMIN_TOKEN -body @{username="newstu";password="123456";studentNo="S9999";name="TestStudentMod";department=@{id=1}}
Test-Step -name "2.3 PUT modify student" -expectedCode 200
Call-API -method Delete -uri "/api/admin/students/9" -token $ADMIN_TOKEN
Test-Step -name "2.4 DELETE student" -expectedCode 200
Call-API -method Get -uri "/api/admin/teachers" -token $ADMIN_TOKEN
Test-Step -name "2.5 GET teachers list" -expectedCode 200
Call-API -method Post -uri "/api/admin/teachers" -token $ADMIN_TOKEN -body @{username="newtch";password="123456";teacherNo="T999";name="TestTeacher";department=@{id=1}}
Test-Step -name "2.6 POST add teacher" -expectedCode 200
Call-API -method Put -uri "/api/admin/teachers/6" -token $ADMIN_TOKEN -body @{username="newtch";password="123456";teacherNo="T999";name="TestTeacherMod";department=@{id=1}}
Test-Step -name "2.7 PUT modify teacher" -expectedCode 200
Call-API -method Delete -uri "/api/admin/teachers/6" -token $ADMIN_TOKEN
Test-Step -name "2.8 DELETE teacher" -expectedCode 200
Call-API -method Get -uri "/api/admin/courses" -token $ADMIN_TOKEN
Test-Step -name "2.9 GET courses list" -expectedCode 200
Call-API -method Post -uri "/api/admin/courses" -token $ADMIN_TOKEN -body @{name="OS";description="OS Course";capacity=60;teacher=@{id=4};department=@{id=1}}
Test-Step -name "2.10 POST add course" -expectedCode 200
Call-API -method Put -uri "/api/admin/courses/4" -token $ADMIN_TOKEN -body @{name="OS-Mod";description="OS Course";capacity=60;teacher=@{id=4};department=@{id=1}}
Test-Step -name "2.11 PUT modify course" -expectedCode 200
Call-API -method Delete -uri "/api/admin/courses/4" -token $ADMIN_TOKEN
Test-Step -name "2.12 DELETE course" -expectedCode 200
Call-API -method Get -uri "/api/admin/enrollments" -token $ADMIN_TOKEN
Test-Step -name "2.13 GET enrollments" -expectedCode 200
Call-API -method Post -uri "/api/admin/enrollments" -token $ADMIN_TOKEN -body @{student=@{id=7};course=@{id=2};status="PENDING"}
Test-Step -name "2.14 POST enrollment" -expectedCode 200
Call-API -method Put -uri "/api/admin/enrollments/6" -token $ADMIN_TOKEN -body @{student=@{id=7};course=@{id=2};status="SELECTED"}
Test-Step -name "2.15 PUT enrollment" -expectedCode 200
Call-API -method Delete -uri "/api/admin/enrollments/6" -token $ADMIN_TOKEN
Test-Step -name "2.16 DELETE enrollment" -expectedCode 200
Call-API -method Post -uri "/api/admin/grades" -token $ADMIN_TOKEN -body @{enrollmentId=1;score=88.5}
Test-Step -name "2.17 POST grade" -expectedCode 200
try { $h = @{"Authorization"="Bearer $ADMIN_TOKEN"}; Invoke-RestMethod -Uri "$BASE/api/admin/stats/chart" -Method Get -Headers $h -OutFile "c:\Users\27355\Desktop\Exercise\COURSE\chart_test.png"; $global:lastCode=200; $global:lastDetail="PNG OK" } catch { $global:lastCode=0; $global:lastDetail="ERR: $($_.Exception.Message)" }
Test-Step -name "2.18 GET chart PNG" -expectedCode 200

# ===== 3. DEPT ADMIN =====
Write-Host "`n--- [3] Dept Admin ---" -ForegroundColor Cyan
Call-API -method Get -uri "/api/dept/students" -token $CSADMIN_TOKEN
Test-Step -name "3.1 GET dept students" -expectedCode 200
Call-API -method Post -uri "/api/dept/students" -token $CSADMIN_TOKEN -body @{username="csstu";password="123456";studentNo="S6666";name="CSDeptStu";department=@{id=1}}
Test-Step -name "3.2 POST dept student" -expectedCode 200
Call-API -method Put -uri "/api/dept/students/10" -token $CSADMIN_TOKEN -body @{username="csstu";password="123456";studentNo="S6666";name="CSDeptStuMod";department=@{id=1}}
Test-Step -name "3.3 PUT dept student" -expectedCode 200
Call-API -method Delete -uri "/api/dept/students/10" -token $CSADMIN_TOKEN
Test-Step -name "3.4 DELETE dept student" -expectedCode 200
Call-API -method Get -uri "/api/dept/teachers" -token $CSADMIN_TOKEN
Test-Step -name "3.5 GET dept teachers" -expectedCode 200
Call-API -method Post -uri "/api/dept/teachers" -token $CSADMIN_TOKEN -body @{username="cstch";password="123456";teacherNo="T666";name="CSDeptTch";department=@{id=1}}
Test-Step -name "3.6 POST dept teacher" -expectedCode 200
Call-API -method Put -uri "/api/dept/teachers/7" -token $CSADMIN_TOKEN -body @{username="cstch";password="123456";teacherNo="T666";name="CSDeptTchMod";department=@{id=1}}
Test-Step -name "3.7 PUT dept teacher" -expectedCode 200
Call-API -method Delete -uri "/api/dept/teachers/7" -token $CSADMIN_TOKEN
Test-Step -name "3.8 DELETE dept teacher" -expectedCode 200
Call-API -method Get -uri "/api/dept/courses" -token $CSADMIN_TOKEN
Test-Step -name "3.9 GET dept courses" -expectedCode 200
Call-API -method Post -uri "/api/dept/courses" -token $CSADMIN_TOKEN -body @{name="CompArch";description="Hardware";capacity=50;teacher=@{id=4};department=@{id=1}}
Test-Step -name "3.10 POST dept course" -expectedCode 200
Call-API -method Put -uri "/api/dept/courses/5" -token $CSADMIN_TOKEN -body @{name="CompArchMod";description="Hardware";capacity=50;teacher=@{id=4};department=@{id=1}}
Test-Step -name "3.11 PUT dept course" -expectedCode 200
Call-API -method Delete -uri "/api/dept/courses/5" -token $CSADMIN_TOKEN
Test-Step -name "3.12 DELETE dept course" -expectedCode 200
Call-API -method Get -uri "/api/dept/enrollments" -token $CSADMIN_TOKEN
Test-Step -name "3.13 GET dept enrollments" -expectedCode 200
Call-API -method Post -uri "/api/dept/enrollments" -token $CSADMIN_TOKEN -body @{student=@{id=7};course=@{id=2};status="PENDING"}
Test-Step -name "3.14 POST dept enrollment" -expectedCode 200
Call-API -method Put -uri "/api/dept/enrollments/7" -token $CSADMIN_TOKEN -body @{student=@{id=7};course=@{id=2};status="SELECTED"}
Test-Step -name "3.15 PUT dept enrollment" -expectedCode 200
Call-API -method Delete -uri "/api/dept/enrollments/7" -token $CSADMIN_TOKEN
Test-Step -name "3.16 DELETE dept enrollment" -expectedCode 200
Call-API -method Post -uri "/api/dept/draw/2" -token $CSADMIN_TOKEN
Test-Step -name "3.17 POST draw course2" -expectedCode 200
Call-API -method Get -uri "/api/dept/draw/2/result" -token $CSADMIN_TOKEN
Test-Step -name "3.18 GET draw result" -expectedCode 200
Call-API -method Post -uri "/api/dept/grades" -token $CSADMIN_TOKEN -body @{enrollmentId=1;score=95.0}
Test-Step -name "3.19 POST dept grade" -expectedCode 200
Call-API -method Get -uri "/api/dept/grades" -token $CSADMIN_TOKEN
Test-Step -name "3.20 GET dept grades" -expectedCode 200
Call-API -method Get -uri "/api/dept/courses" -token $MATHADMIN_TOKEN
Test-Step -name "3.21 GET math dept courses" -expectedCode 200

# ===== 4. TEACHER =====
Write-Host "`n--- [4] Teacher ---" -ForegroundColor Cyan
Call-API -method Put -uri "/api/teacher/courses/1" -token $TEACHER1_TOKEN -body @{name="Java Basic";description="Updated desc";capacity=100}
Test-Step -name "4.1 PUT update course" -expectedCode 200
Call-API -method Put -uri "/api/teacher/courses/2/prerequisite" -token $TEACHER1_TOKEN -body @{prerequisiteId=1}
Test-Step -name "4.2 PUT set prereq" -expectedCode 200
Call-API -method Put -uri "/api/teacher/courses/2/prerequisite" -token $TEACHER1_TOKEN -body @{prerequisiteId=$null}
Test-Step -name "4.3 PUT clear prereq" -expectedCode 200
try { $h = @{"Authorization"="Bearer $TEACHER1_TOKEN"}; $f = "c:\Users\27355\Desktop\Exercise\COURSE\Student\api_test.ps1"; $resp = Invoke-RestMethod -Uri "$BASE/api/teacher/courses/1/courseware" -Method Post -Headers $h -Form @{file=(Get-Item $f)}; $global:lastCode=$resp.code; $global:lastDetail=($resp|ConvertTo-Json -Compress -Depth 10) } catch { $global:lastCode=0; $global:lastDetail="ERR: $($_.Exception.Message)" }
Test-Step -name "4.4 POST upload courseware" -expectedCode 200
Call-API -method Get -uri "/api/teacher/courses/1/courseware" -token $TEACHER1_TOKEN
Test-Step -name "4.5 GET courseware list" -expectedCode 200
Call-API -method Put -uri "/api/teacher/courses/2/prerequisite" -token $TEACHER1_TOKEN -body @{prerequisiteId=1}
Test-Step -name "4.6 PUT restore prereq" -expectedCode 200
$b2 = '{"courseId":2}'
try { Invoke-RestMethod -Uri "$BASE/api/student/enroll" -Method Post -Headers @{"Authorization"="Bearer $STUDENT1_TOKEN"} -ContentType "application/json" -Body $b2 } catch {}
Call-API -method Post -uri "/api/dept/draw/2" -token $CSADMIN_TOKEN
Test-Step -name "4.7 POST redraw for teacher" -expectedCode 200
Call-API -method Get -uri "/api/teacher/enrollments/selected" -token $TEACHER1_TOKEN
Test-Step -name "4.8 GET selected students" -expectedCode 200
Call-API -method Post -uri "/api/teacher/grades" -token $TEACHER1_TOKEN -body @{enrollmentId=1;score=90.0}
Test-Step -name "4.9 POST teacher grade" -expectedCode 200
Call-API -method Get -uri "/api/teacher/stats/1" -token $TEACHER1_TOKEN
Test-Step -name "4.10 GET course stats" -expectedCode 200
Call-API -method Post -uri "/api/teacher/messages" -token $TEACHER1_TOKEN -body @{courseId=2;studentId=6;content="Hello student"}
Test-Step -name "4.11 POST teacher msg" -expectedCode 200
Call-API -method Get -uri "/api/teacher/messages" -token $TEACHER1_TOKEN
Test-Step -name "4.12 GET teacher msgs" -expectedCode 200

# ===== 5. STUDENT =====
Write-Host "`n--- [5] Student ---" -ForegroundColor Cyan
Call-API -method Get -uri "/api/student/info" -token $STUDENT1_TOKEN
Test-Step -name "5.1 GET student info" -expectedCode 200
Call-API -method Get -uri "/api/student/courses" -token $STUDENT1_TOKEN
Test-Step -name "5.2 GET all courses" -expectedCode 200
Call-API -method Post -uri "/api/student/enroll" -token $STUDENT3_TOKEN -body @{courseId=1}
Test-Step -name "5.3 POST enroll(no prereq)" -expectedCode 200
Call-API -method Post -uri "/api/student/enroll" -token $STUDENT1_TOKEN -body @{courseId=2}
Test-Step -name "5.4 POST enroll(w prereq met)" -expectedCode 200
Call-API -method Post -uri "/api/student/enroll" -token $STUDENT3_TOKEN -body @{courseId=2}
Test-Step -name "5.5 POST enroll(prereq fail)" -expectedCode 400
Call-API -method Get -uri "/api/student/enrollments" -token $STUDENT1_TOKEN
Test-Step -name "5.6 GET my enrollments" -expectedCode 200
Call-API -method Get -uri "/api/student/grades" -token $STUDENT1_TOKEN
Test-Step -name "5.7 GET my grades" -expectedCode 200
Call-API -method Post -uri "/api/student/messages" -token $STUDENT1_TOKEN -body @{courseId=2;teacherId=4;content="Hello teacher"}
Test-Step -name "5.8 POST student msg" -expectedCode 200
Call-API -method Get -uri "/api/student/messages" -token $STUDENT1_TOKEN
Test-Step -name "5.9 GET student msgs" -expectedCode 200
Call-API -method Get -uri "/api/student/draw-result" -token $STUDENT1_TOKEN
Test-Step -name "5.10 GET draw result" -expectedCode 200
try { $h = @{"Authorization"="Bearer $STUDENT1_TOKEN"}; Invoke-RestMethod -Uri "$BASE/api/student/courseware/1" -Method Get -Headers $h -OutFile "c:\Users\27355\Desktop\Exercise\COURSE\dl_cw_test"; $global:lastCode=200; $global:lastDetail="Download OK" } catch { $global:lastCode=0; $global:lastDetail="ERR: $($_.Exception.Message)" }
Test-Step -name "5.11 GET download courseware" -expectedCode 200

# ===== 6. PERMISSION =====
Write-Host "`n--- [6] Permission ---" -ForegroundColor Cyan
Call-API -method Get -uri "/api/admin/students"
Test-Step -name "6.1 No token" -expectedCode 401
Call-API -method Get -uri "/api/admin/students" -token $STUDENT1_TOKEN
Test-Step -name "6.2 Student access admin" -expectedCode 403

# ===== REPORT =====
Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "              TEST REPORT" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Total: $($global:testNum) | Pass: $($global:passed) | Fail: $($global:failed)" -ForegroundColor White
$global:results | Format-Table No, Status, Name, ExpectedCode, ActualCode -AutoSize
$rp = "c:\Users\27355\Desktop\Exercise\COURSE\api_test_report.csv"
$global:results | Export-Csv -Path $rp -NoTypeInformation -Encoding UTF8
Write-Host "Report exported to: $rp" -ForegroundColor Green
