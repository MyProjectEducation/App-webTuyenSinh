const db = require('../../config/db'); // Đường dẫn chuẩn của dự án bạn là ../../config/db

// 1. Lấy Thống kê Tổng quan
exports.getOverviewStats = async (req, res) => {
    try {
        const promiseCan = db.query('SELECT COUNT(*) AS total FROM xt_thisinhxettuyen25');
        const promiseAsp = db.query('SELECT COUNT(*) AS total FROM xt_nguyenvongxettuyen');
        const promiseTar = db.query('SELECT SUM(n_chitieu) AS total FROM xt_nganh');

        // Chạy 3 câu query cùng lúc
        const [resCan, resAsp, resTar] = await Promise.all([promiseCan, promiseAsp, promiseTar]);

        res.status(200).json({
            totalCandidates: resCan[0][0].total || 0,
            totalAspirations: resAsp[0][0].total || 0,
            totalTargets: resTar[0][0].total || 0
        });
    } catch (error) {
        res.status(500).json({ message: "Lỗi truy xuất tổng quan", error });
    }
};

// 2. Lấy Top 5 Ngành "Hot" nhất (Dựa trên số lượng NV đăng ký)
exports.getTopMajors = async (req, res) => {
    try {
        const query = `
            SELECT n.tennganh as name, COUNT(nv.idnv) as value 
            FROM xt_nguyenvongxettuyen nv 
            JOIN xt_nganh n ON nv.nv_manganh = n.manganh 
            GROUP BY nv.nv_manganh, n.tennganh 
            ORDER BY value DESC 
            LIMIT 5
        `;
        const [rows] = await db.query(query);
        res.status(200).json(rows);
    } catch (error) {
        res.status(500).json({ message: "Lỗi truy xuất Top Ngành", error });
    }
};

// 3. Lấy Phổ điểm xét tuyển
exports.getScoreDistribution = async (req, res) => {
    try {
        const query = `
            SELECT 
                CASE 
                    WHEN diem_xettuyen < 15 THEN 'Dưới 15' 
                    WHEN diem_xettuyen >= 15 AND diem_xettuyen < 20 THEN '15 - 20' 
                    WHEN diem_xettuyen >= 20 AND diem_xettuyen < 25 THEN '20 - 25' 
                    WHEN diem_xettuyen >= 25 THEN 'Từ 25 trở lên' 
                END AS range_name,
                COUNT(*) AS count
            FROM xt_nguyenvongxettuyen 
            WHERE diem_xettuyen IS NOT NULL
            GROUP BY range_name
            ORDER BY MIN(diem_xettuyen) ASC
        `;
        const [rows] = await db.query(query);
        res.status(200).json(rows);
    } catch (error) {
        res.status(500).json({ message: "Lỗi truy xuất Phổ điểm", error });
    }
};
