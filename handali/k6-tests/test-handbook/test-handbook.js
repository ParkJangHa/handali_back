import http from 'k6/http';
import { check, sleep } from 'k6';

// 받은 토큰 넣기
const TOKEN = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYWRhMTExMUBnbWFpbC5jb20iLCJ1c2VySWQiOjUyLCJ0eXBlIjoiYWNjZXNzIiwiaWF0IjoxNzcyMzQxMzEwLCJleHAiOjE3NzIzNDIyMTB9.q2QAYCGBlJlgou8OFTkkia3OKeCAIXslg5qacqpX748';

export let options = {
    stages: [
        { duration: '20s', target: 10 },   // 10명
        { duration: '30s', target: 10 },
        { duration: '20s', target: 50 },   // 50명
        { duration: '30s', target: 50 },
        { duration: '20s', target: 100 },  // 100명
        { duration: '30s', target: 100 },
        { duration: '20s', target: 0 },
    ],
};

export default function() {
    let res = http.get('http://localhost:8080/handbooks', {
        headers: {
            'Authorization': `Bearer ${TOKEN}`,
        },
    });

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1);
}