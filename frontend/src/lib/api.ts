const API_URL =
  process.env.NEXT_PUBLIC_API_URL ||
  "http://localhost:3000/api";

// =====================
// MAHASISWA
// =====================

export type Mahasiswa = {
  id: number;
  nim: string;
  nama: string;
  prodi_id: number;
  nama_prodi: string;
  angkatan: number;
  foto?: string | null;
};

export type Prodi = {
  id: number;
  nama_prodi: string;
};

export type MahasiswaResponse = {
  message: string;
  data: Mahasiswa[];
  meta: {
    page: number;
    limit: number;
    total: number;
    totalPage: number;
  };
};

// mengambil token JWT
function getToken() {
  if (typeof window !== "undefined") {
    return localStorage.getItem("token");
  }

  return null;
}

// =====================
// GET MAHASISWA
// =====================

export async function getMahasiswa(params?: {
  search?: string;
  prodi_id?: string;
  page?: number;
  limit?: number;
}): Promise<MahasiswaResponse> {

  const query = new URLSearchParams();

  if (params?.search)
    query.set("search", params.search);

  if (params?.prodi_id)
    query.set("prodi_id", params.prodi_id);

  if (params?.page)
    query.set("page", String(params.page));

  if (params?.limit)
    query.set("limit", String(params.limit));

  const response = await fetch(
    `${API_URL}/mahasiswa?${query.toString()}`,
    {
      cache: "no-store",
      headers: {
        Authorization: `Bearer ${getToken()}`
      }
    }
  );

  const result = await response.json();

  if (!response.ok) {
    throw new Error(
      result.message ||
      "Gagal mengambil data mahasiswa"
    );
  }

  return result;
}

// =====================
// GET PRODI
// =====================

export async function getProdi(): Promise<Prodi[]> {

  const response = await fetch(
    `${API_URL}/prodi`,
    {
      cache: "no-store"
    }
  );

  const result = await response.json();

  if (!response.ok) {
    throw new Error(
      result.message ||
      "Gagal mengambil data prodi"
    );
  }

  return result.data || [];
}

// =====================
// CREATE MAHASISWA
// =====================

export async function createMahasiswa(
  formData: FormData
) {

  const response = await fetch(
    `${API_URL}/mahasiswa`,
    {
      method: "POST",
      headers: {
        Authorization: `Bearer ${getToken()}`
      },
      body: formData
    }
  );

  const result = await response.json();

  if (!response.ok) {
    throw new Error(
      result.message ||
      "Gagal menambah mahasiswa"
    );
  }

  return result;
}

// =====================
// UPDATE MAHASISWA
// =====================

export async function updateMahasiswa(
  id: number,
  formData: FormData
) {

  const response = await fetch(
    `${API_URL}/mahasiswa/${id}`,
    {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${getToken()}`
      },
      body: formData
    }
  );

  const result = await response.json();

  if (!response.ok) {
    throw new Error(
      result.message ||
      "Gagal memperbarui mahasiswa"
    );
  }

  return result;
}

// =====================
// DELETE MAHASISWA
// =====================

export async function deleteMahasiswa(
  id: number
) {

  const response = await fetch(
    `${API_URL}/mahasiswa/${id}`,
    {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${getToken()}`
      }
    }
  );

  const result = await response.json();

  if (!response.ok) {
    throw new Error(
      result.message ||
      "Gagal menghapus mahasiswa"
    );
  }

  return result;
}

// =====================
// PRODUK
// =====================

export type Produk = {
  id: number;
  nama: string;
  harga: number;
  stok: number;
};

export type ProdukInput = {
  nama: string;
  harga: number;
  stok: number;
};

type ApiResponse<T> = {
  message: string;
  data?: T;
};

async function handleResponse<T>(
  response: Response
): Promise<ApiResponse<T>> {

  const result = await response.json();

  if (!response.ok) {
    throw new Error(
      result.message ||
      "Terjadi kesalahan saat mengakses API"
    );
  }

  return result;
}

// =====================
// GET PRODUK
// =====================

export async function getProduk(): Promise<Produk[]> {

  const response = await fetch(
    `${API_URL}/produk`,
    {
      cache: "no-store"
    }
  );

  const result =
    await handleResponse<Produk[]>(response);

  return result.data || [];
}

// =====================
// CREATE PRODUK
// =====================

export async function createProduk(
  payload: ProdukInput
): Promise<Produk> {

  const response = await fetch(
    `${API_URL}/produk`,
    {
      method: "POST",
      headers: {
        "Content-Type":
          "application/json",
      },
      body: JSON.stringify(payload)
    }
  );

  const result =
    await handleResponse<Produk>(response);

  return result.data as Produk;
}

// =====================
// UPDATE PRODUK
// =====================

export async function updateProduk(
  id: number,
  payload: ProdukInput
): Promise<void> {

  const response = await fetch(
    `${API_URL}/produk/${id}`,
    {
      method: "PUT",
      headers: {
        "Content-Type":
          "application/json",
      },
      body: JSON.stringify(payload)
    }
  );

  await handleResponse(response);
}

// =====================
// DELETE PRODUK
// =====================

export async function deleteProduk(
  id: number
): Promise<void> {

  const response = await fetch(
    `${API_URL}/produk/${id}`,
    {
      method: "DELETE"
    }
  );

  await handleResponse(response);
}