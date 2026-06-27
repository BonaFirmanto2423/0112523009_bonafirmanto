"use client";

import { useEffect, useState } from "react";

import {
  getMahasiswa,
  getProdi,
  createMahasiswa,
  updateMahasiswa,
  deleteMahasiswa,
  Mahasiswa,
  Prodi,
} from "@/lib/api";

import {
  getUser,
  logout,
} from "@/lib/auth";

const BACKEND_URL =
  process.env.NEXT_PUBLIC_BACKEND_URL ||
  "http://localhost:3000";

export default function MahasiswaPage() {

  // =====================
  // STATE
  // =====================

  const [mahasiswa, setMahasiswa] =
    useState<Mahasiswa[]>([]);

  const [prodi, setProdi] =
    useState<Prodi[]>([]);

  const [loading, setLoading] =
    useState(true);

  // form

  const [nim, setNim] =
    useState("");

  const [nama, setNama] =
    useState("");

  const [angkatan, setAngkatan] =
    useState("");

  const [selectedProdi, setSelectedProdi] =
    useState("");

  const [foto, setFoto] =
    useState<File | null>(null);

  const [editingId, setEditingId] =
    useState<number | null>(null);

  // search

  const [search, setSearch] =
    useState("");

  // user

  const [user, setUser] = useState<any>(null);

useEffect(() => {
  setUser(getUser());
}, []);

const role = user?.role;

  const canCreate =
    role === "admin" ||
    role === "operator";

  const canEdit =
    role === "admin" ||
    role === "operator";

  const canDelete =
    role === "admin";

  // =====================
  // LOAD DATA
  // =====================

  async function loadData() {

    try {

      setLoading(true);

      const result =
        await getMahasiswa();

      console.log(
        "HASIL API:",
        result
      );

      setMahasiswa(
        result.data || []
      );

    } catch (error) {

      console.log(
        "ERROR:",
        error
      );

    } finally {

      setLoading(false);

    }
  }

  async function loadProdi() {

    try {

      const result =
        await getProdi();

      setProdi(result);

    } catch (error) {

      console.log(error);

    }
  }

  // =====================
  // TAMBAH / EDIT
  // =====================

  async function handleSubmit(
    e: React.FormEvent
  ) {

    e.preventDefault();

    try {

      const formData =
        new FormData();

      formData.append(
        "nim",
        nim
      );

      formData.append(
        "nama",
        nama
      );

      formData.append(
        "prodi_id",
        selectedProdi
      );

      formData.append(
        "angkatan",
        angkatan
      );

      if (foto) {

        formData.append(
          "foto",
          foto
        );

      }

      if (editingId) {

        await updateMahasiswa(
          editingId,
          formData
        );

        alert(
          "Data berhasil diupdate"
        );

      } else {

        await createMahasiswa(
          formData
        );

        alert(
          "Data berhasil ditambah"
        );

      }

      // reset

      setNim("");
      setNama("");
      setAngkatan("");
      setSelectedProdi("");
      setFoto(null);
      setEditingId(null);

      loadData();

    } catch (error: any) {

      alert(
        error.message
      );

    }
  }

  // =====================
  // EDIT
  // =====================

  function handleEdit(
    item: Mahasiswa
  ) {

    setEditingId(
      item.id
    );

    setNim(item.nim);

    setNama(item.nama);

    setAngkatan(
      String(item.angkatan)
    );

    setSelectedProdi(
      String(item.prodi_id)
    );

    window.scrollTo({
      top: 0,
      behavior: "smooth"
    });
  }

  // =====================
  // DELETE
  // =====================

  async function handleDelete(
    id: number
  ) {

    const yakin =
      confirm(
        "Yakin hapus data?"
      );

    if (!yakin) return;

    try {

      await deleteMahasiswa(
        id
      );

      alert(
        "Data berhasil dihapus"
      );

      loadData();

    } catch (error: any) {

      alert(
        error.message
      );

    }
  }

  // =====================
  // SEARCH
  // =====================

  const filteredMahasiswa =
    mahasiswa.filter((item) => {

      return (
        item.nama
          .toLowerCase()
          .includes(
            search.toLowerCase()
          ) ||

        item.nim
          .toLowerCase()
          .includes(
            search.toLowerCase()
          )
      );

    });

  // =====================
  // EFFECT
  // =====================

  useEffect(() => {

    loadData();
    loadProdi();

  }, []);

  // =====================
  // RENDER
  // =====================

  return (

    <div
      style={{
        padding: "30px"
      }}
    >

      <h1>
        Data Mahasiswa
      </h1>

      <p>

       
  Login sebagai:
  <b> {user?.name || "-"}</b>
  ({user?.role || "-"})
</p>

      <button
        onClick={logout}
      >
        Logout
      </button>

      <hr />

      {/* FORM */}

      {canCreate && (

        <>

          <h2>

            {
              editingId
                ? "Edit Mahasiswa"
                : "Tambah Mahasiswa"
            }

          </h2>

          <form
            onSubmit={
              handleSubmit
            }
          >

            <input
              placeholder="NIM"
              value={nim}
              onChange={(e) =>
                setNim(
                  e.target.value
                )
              }
            />

            <br /><br />

            <input
              placeholder="Nama"
              value={nama}
              onChange={(e) =>
                setNama(
                  e.target.value
                )
              }
            />

            <br /><br />

            <select
              value={selectedProdi}
              onChange={(e) =>
                setSelectedProdi(
                  e.target.value
                )
              }
            >

              <option value="">
                Pilih Prodi
              </option>

              {prodi.map((item) => (

                <option
                  key={item.id}
                  value={item.id}
                >
                  {item.nama_prodi}
                </option>

              ))}

            </select>

            <br /><br />

            <input
              type="number"
              placeholder="Angkatan"
              value={angkatan}
              onChange={(e) =>
                setAngkatan(
                  e.target.value
                )
              }
            />

            <br /><br />

            <input
              type="file"
              onChange={(e) => {

                if (
                  e.target.files &&
                  e.target.files[0]
                ) {

                  setFoto(
                    e.target.files[0]
                  );

                }

              }}
            />

            <br /><br />

            <button
              type="submit"
            >

              {
                editingId
                  ? "Update"
                  : "Simpan"
              }

            </button>

          </form>

          <hr />

        </>

      )}

      {/* SEARCH */}

      <input
        placeholder="Cari nama / NIM"
        value={search}
        onChange={(e) =>
          setSearch(
            e.target.value
          )
        }
      />

      <br /><br />

      {/* TABLE */}

      {loading ? (

        <p>Loading...</p>

      ) : (

        <table
          border={1}
          cellPadding={10}
          width="100%"
        >

          <thead>

            <tr>
              <th>Foto</th>
              <th>NIM</th>
              <th>Nama</th>
              <th>Prodi</th>
              <th>Angkatan</th>
              <th>Aksi</th>
            </tr>

          </thead>

          <tbody>

            {filteredMahasiswa.map(
              (item) => (

                <tr
                  key={item.id}
                >

                  <td>

                    <img
                      src={
                        item.foto
                          ? `${BACKEND_URL}/uploads/${item.foto}`
                          : "/avatar-placeholder.png"
                      }
                      width={60}
                      height={60}
                      alt={item.nama}
                    />

                  </td>

                  <td>
                    {item.nim}
                  </td>

                  <td>
                    {item.nama}
                  </td>

                  <td>
                    {item.nama_prodi}
                  </td>

                  <td>
                    {item.angkatan}
                  </td>

                  <td>

                    {canEdit && (

                      <button
                        onClick={() =>
                          handleEdit(
                            item
                          )
                        }
                      >
                        Edit
                      </button>

                    )}

                    {" "}

                    {canDelete && (

                      <button
                        onClick={() =>
                          handleDelete(
                            item.id
                          )
                        }
                      >
                        Hapus
                      </button>

                    )}

                  </td>

                </tr>

              )
            )}

          </tbody>

        </table>

      )}

    </div>

  );
}