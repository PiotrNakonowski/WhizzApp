import * as React from 'react';
import { useEffect, useState } from 'react';
import { styled } from '@mui/material/styles';
import { DataGrid } from '@mui/x-data-grid';
import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';
import { collection, getDocs, deleteDoc, doc} from 'firebase/firestore';
import { db } from '../../firebase'; 

const StyledTableContainer = styled(Paper)({
  backgroundColor: 'white',
  marginBottom: '20px',
  height: 700,
  width: '100%',
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

  useEffect(() => {
    const fetchData = async () => {
      let list = [];
      try {
       // const colRef = collection(db, "users");
       // getDocs(colRef)
       //   .then((snapshot) => {console.log(snapshot.docs)}) 
        const querySnapshot = await getDocs(collection(db, "users"));
        querySnapshot.forEach((doc) => {
          const data = doc.data();
          const createdAt = data.createdAt ? data.createdAt.toDate().toISOString().split('T')[0] : '';
          list.push({ id: doc.id, ...data, createdAt });
        });
        setData(list);
        console.log(list);
      } catch (err) {
        console.log(err);
      }
    };

    fetchData();

  }, []);

  const handleDelete = async (id) => {
    try {
      await deleteDoc(doc(db, "users", id));
      setData(data.filter((item) => item.id !== id));
    } catch (err) {
      console.log(err);
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
      renderCell: (params) => (
        <Button
          variant="contained"
          style={{ backgroundColor: '#D87648', color: 'white' }}
          onClick={() => handleDelete(params.row.id)}
        >
          Usuń
        </Button>
      ),
    },
  ];

  return (
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
  );
}

export default UserTable;