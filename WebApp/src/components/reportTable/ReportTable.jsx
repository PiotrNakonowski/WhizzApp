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
  '& .MuiDataGrid-cell': {
    paddingTop: '0.5em',
    paddingBottom: '0.5em',
    paddingLeft: '0.5em',
    paddingRight: '0.5em',
  },
}));

const ReportTable = () => {
  
  const [data, setData] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      let list = [];
      try {
        const querySnapshot = await getDocs(collection(db, "reports"));
        querySnapshot.forEach((doc) => {
          const reportData = doc.data();
            const report_row = {
              id: doc.id,
              Id_report: doc.id,
              Id_user: reportData.Owner,
              description: reportData.Description,
              date: reportData.Date?.toDate().toISOString().split('T')[0],
            };
            list.push(report_row);
        });
        setData(list);
      } catch (err) {
        console.log(err);
      }
    };

    fetchData();
  }, []);

  const handleDelete = async (id) => {
    try {
      await deleteDoc(doc(db, "reports", id));
      setData(data.filter((item) => item.id !== id));
    } catch (err) {
      console.log(err);
    }
  };
  
  const columns = [
    { field: 'Id_report', headerName: 'Id_ticket', flex: 2 },
    { field: 'Id_user', headerName: 'Id_user', flex: 2 },
    { 
        field: 'description', 
        headerName: 'Opis', 
        flex: 2,
        sortable: false,
        renderCell: (params) => (
            <div style={{ whiteSpace: 'normal', wordBreak: 'break-word',lineHeight: '1.5em', height: 'auto', display: 'block'}}>
            {params.value}
          </div>
        ),
    },
    { field: 'date', headerName: 'Data złożenia', flex: 1 },
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
    <StyledTableContainer>
      <StyledDataGrid
        rows={data}
        columns={columns}
        pageSizeOptions={[5, 10]}
        rowHeight={110}
        initialState={{
          pagination: {
            paginationModel: { page: 0, pageSize: 10 },
          },
        }}
        disableSelectionOnClick
      />
    </StyledTableContainer>
  );
};

export default ReportTable;
