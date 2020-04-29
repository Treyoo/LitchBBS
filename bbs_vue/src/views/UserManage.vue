<template>
    <div>
        <el-table
                :data="tableData"
                border
                stripe
                style="width: 65%"
                height="450">
            <el-table-column
                    fixed
                    prop="id"
                    label="编号"
                    width="150">
            </el-table-column>
            <el-table-column
                    prop="username"
                    label="用户名"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="email"
                    label="邮箱"
                    width="120">
            </el-table-column>
            <el-table-column
                    prop="type"
                    label="type"
                    width="65">
            </el-table-column>
            <el-table-column
                    prop="status"
                    label="status"
                    width="65">
            </el-table-column>
            <!--<el-table-column
                    prop="headerUrl"
                    label="headerUrl"
                    width="120">
            </el-table-column>-->
            <el-table-column
                    fixed="right"
                    label="操作"
                    width="100">
                <template slot-scope="scope">
                    <el-button @click="edit(scope.row)" type="text" size="small">修改</el-button>
                    <el-button @click="deleteBook(scope.row)" type="text" size="small">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-pagination
                background
                layout="prev, pager, next"
                :page-size="pageSize"
                :total="total"
                @current-change="page">
        </el-pagination>
    </div>
</template>

<script>
    export default {
        methods: {
            deleteBook(row){
                const _this = this
                axios.delete('http://localhost:8181/book/deleteById/'+row.id).then(function(resp){
                    _this.$alert('《'+row.name+'》删除成功！', '消息', {
                        confirmButtonText: '确定',
                        callback: action => {
                            window.location.reload()
                        }
                    })
                })
            },
            edit(row) {
                this.$router.push({
                    path: '/update',
                    query:{
                        id:row.id
                    }
                })
            },
            page(currentPage){
                const _this = this
                const pageSize = 5
                const offset = (currentPage-1)*pageSize
                axios.get('http://localhost:8080/manage/user?offset='+offset+"&limit="+pageSize)
                    .then(function(resp){
                      console.log(resp)
                      _this.tableData = resp.data.users
                      _this.pageSize = pageSize
                      _this.total = resp.data.total
                })
            }
        },

        data() {
            return {
                pageSize:1,
                total:11,
                tableData: [{
                    id: 1,
                    name: '解忧杂货店',
                    author: '东野圭吾'
                }, {
                    id: 2,
                    name: '追风筝的人',
                    author: '卡勒德·胡赛尼'
                }, {
                    id: 3,
                    name: '人间失格',
                    author: '太宰治'
                }]
            }
        },

        created() {
            const _this = this
            const pageSize = 5
            axios.get('http://localhost:8080/manage/user').then(function(resp){
                console.log(resp)
                _this.tableData = resp.data.users
                _this.pageSize = pageSize
                _this.total = resp.data.total
            })
        }
    }
</script>
