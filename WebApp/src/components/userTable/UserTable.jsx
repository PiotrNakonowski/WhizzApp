import * as React from 'react';
import { useEffect, useState } from 'react';
import { styled } from '@mui/material/styles';
import { DataGrid } from '@mui/x-data-grid';
import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';
import { collection, getDocs } from 'firebase/firestore';
import { db } from '../../firebase'; 
import { functions } from '../../firebase';
import { httpsCallable } from "firebase/functions";
import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';



const StyledTableContainer = styled(Paper)({
  backgroundColor: 'white',
  marginBottom: '20px',
  height: 700,
  width: '100%',
  boxShadow: 'none',
});

const StyledDataGrid = styled(DataGrid)(() => ({
  '& .MuiDataGrid-columnHeaderTitleContainer .MuiButtonBase-root': {
    color: 'white',
    fontSize: 20,
  },
  '& .MuiDataGrid-root': {
    backgroundColor: '#D1CBCB',
  },
  '& .MuiDataGrid-columnSeparator': {
    display: 'none', 
  },
  '& .MuiDataGrid-columnHeaders': {
    backgroundColor: '#616161',
    color: 'white',
    fontSize: 20,
  },
  '& .MuiDataGrid-columnsContainer, .MuiDataGrid-cell': {
    fontSize: 16,
  },
  '& .MuiDataGrid-row:nth-of-type(odd)': {
    backgroundColor: '#EDEDED',
  },
  '& .MuiDataGrid-row:nth-of-type(even)': {
    backgroundColor: 'white',
  },
  '& .MuiDataGrid-row:hover:nth-of-type(even)': {
    backgroundColor: 'white !important',
  },
  '& .MuiDataGrid-row:hover:nth-of-type(odd)': {
    backgroundColor: '#EDEDED !important', 
  },
  '& .MuiDataGrid-columnHeader:focus, & .MuiDataGrid-columnHeader:focus-within': {
    outline: 'none',
  },
  '& .MuiButtonBase-root:focus': {
    outline: 'none',
  },
  '& .MuiButtonBase-root': {
    outline: 'none',
  },
  '& .MuiDataGrid-cell:focus, & .MuiDataGrid-cell:focus-within': {
    outline: 'none !important', 
    boxShadow: 'none !important', 
  },
}));

const UserTable = () => {
  
  const [data, setData] = useState([]);
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      let list = [];
      try {
        const querySnapshot = await getDocs(collection(db, "users"));
        querySnapshot.forEach((doc) => {
            const data = doc.data();
            if (!data.isAdmin) {
            const createdAt = data.createdAt ? data.createdAt.toDate().toISOString().split('T')[0] : '';
            list.push({ id: doc.id, ...data, createdAt });
          }
        });
        setData(list);
      } catch (err) {
        console.log(err);
      }
    };

    fetchData();

  }, []);

  const handleDelete = async (id) => {
    setIsDeleting(true);
    const deleteUser = httpsCallable(functions, 'deleteUser');
  
    try {
      const result = await deleteUser({ uid: id });
  
      if (result.data.success) {
        setData(data.filter((item) => item.id !== id));
      } else {
        console.error("Error deleting user:", result.data.error);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setIsDeleting(false);
    }
  };
  
  
  const columns = [
    { field: 'id', headerName: 'Id_user', flex: 2 },
    { field: 'name', headerName: 'Imię', flex: 1 },
    { field: 'surname', headerName: 'Nazwisko', flex: 1 },
    { field: 'email', headerName: 'Email', flex: 2 },
    { field: 'createdAt', headerName: 'Data dołączenia', flex: 1 },
    {
      field: 'action',
      headerName: 'Akcja',
      sortable: false,
      flex: 1,
      headerAlign: 'center',
      renderCell: (params) => (
        <div style={{ display: 'flex', justifyContent: 'center', width: '100%' }}>
        <Button
          variant="contained"
          style={{ backgroundColor: '#D87648', color: 'white' }}
          onClick={() => handleDelete(params.row.id)}
        >
          Usuń
        </Button>
        </div>
      ),
    },
  ];

  return (
    <>
    <StyledTableContainer>
      <StyledDataGrid
        rows={data}
        columns={columns}
        pageSizeOptions={[5, 10]}
        initialState={{
          pagination: {
            paginationModel: { page: 0, pageSize: 10 },
          },
        }}
        disableSelectionOnClick
      />
    </StyledTableContainer>
    <Backdrop
    sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
    open={isDeleting}
  >
    <CircularProgress color="inherit" />
  </Backdrop>
  </>
  );
}

export default UserTable;

  
  