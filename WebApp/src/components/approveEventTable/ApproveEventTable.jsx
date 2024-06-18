import * as React from 'react';
import { useEffect, useState } from 'react';
import { styled } from '@mui/material/styles';
import { DataGrid } from '@mui/x-data-grid';
import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';
import { collection, getDocs, deleteDoc, doc, updateDoc } from 'firebase/firestore';
import { db } from '../../firebase';

const StyledTableContainer = styled(Paper)(({hasRows}) => ({
  backgroundColor: 'white',
  marginBottom: '20px',
  height: hasRows ? 500 : 325,
  width: '100%',
  boxShadow: 'none',
}));

const StyledDataGrid = styled(DataGrid)(() => ({
  '& .MuiDataGrid-columnHeaderTitleContainer .MuiButtonBase-root': {
    color: 'white',
    fontSize: 20,
  },
  '& .MuiDataGrid-root': {
    backgroundColor: '#D1CBCB',
    ShadowRoot: 'none',
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

const ApproveEventTable = () => {
  const [data, setData] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      let list = [];
      try {
        const querySnapshot = await getDocs(collection(db, "events"));
        querySnapshot.forEach((doc) => {
          const eventData = doc.data();
          if (!eventData.Approved) {
            const event = {
              id: doc.id,
              Id_event: doc.id,
              Id_user: eventData.Owner,
              title: eventData.Title,
              description: eventData.Description,
              date: eventData.CreatedAt?.toDate().toISOString().split('T')[0],
            };
            list.push(event);
          }
        });
        setData(list);
      } catch (err) {
        console.log(err);
      }
    };

    fetchData();
  }, []);

  const handleApprove = async (id) => {
    try {
      const eventDoc = doc(db, "events", id);
      await updateDoc(eventDoc, { Approved: true });
      setData(data.filter((item) => item.id !== id));
    } catch (err) {
      console.log(err);
    }
  };

  const handleReject = async (id) => {
    try {
      await deleteDoc(doc(db, "events", id));
      setData(data.filter((item) => item.id !== id));
    } catch (err) {
      console.log(err);
    }
  };

  const columns = [
    { field: 'Id_event', headerName: 'Id_event', flex: 2 },
    { field: 'Id_user', headerName: 'Id_user', flex: 3 },
    { field: 'title', headerName: 'Tytuł', flex: 1 },
    {
      field: 'description',
      headerName: 'Opis',
      flex: 2,
      sortable: false,
      renderCell: (params) => (
        <div style={{ whiteSpace: 'normal', wordBreak: 'break-word', lineHeight: '1.5em', height: 'auto', display: 'block' }}>
          {params.value}
        </div>
      ),
    },
    { field: 'date', headerName: 'Data utworzenia', flex: 1 },
    {
      field: 'action',
      headerName: 'Akcja',
      sortable: false,
      flex: 2,
      headerAlign: 'center',
      renderCell: (params) => (
        <div style={{ display: 'flex', justifyContent: 'center', width: '100%' }}>
          <Button
            variant="contained"
            style={{ backgroundColor: '#4CAF50', color: 'white', minWidth: 'auto', width: 'auto', marginRight: '8px' }}
            onClick={() => handleApprove(params.row.id)}
          >
            Zatwierdź
          </Button>
          <Button
            variant="contained"
            style={{ backgroundColor: '#D87648', color: 'white', minWidth: 'auto', width: 'auto' }}
            onClick={() => handleReject(params.row.id)}
          >
            Odrzuć
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
        localeText={{
          noRowsLabel: 'Brak nowych wydarzeń',
        }}
      />
    </StyledTableContainer>
  );
};

export default ApproveEventTable;
