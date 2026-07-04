import express from "express";
import cors from "cors";
import path from "path";

import mahasiswaRoutes from "./routes/mahasiswa.route";
import prodiRoutes from "./routes/prodi.route";
import authRoutes from "./routes/auth.route";

const app = express();

app.use(
  cors({
    origin: "http://localhost:3001",
    credentials: true,
  })
);

app.use(express.json());

app.use(
  "/uploads",
  express.static(path.join(process.cwd(), "uploads"))
);

app.get("/", (req, res) => {
  res.json({
    message: "Backend Express berjalan",
  });
});

app.use("/api/mahasiswa", mahasiswaRoutes);
app.use("/api/prodi", prodiRoutes);
app.use("/api/auth", authRoutes);

export default app;