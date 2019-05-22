module.exports = (sequelize, DataTypes) => {
    let post = sequelize.define('post', {
        id:         { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
        title:      { type: DataTypes.STRING(30), unique: true, allowNull: false },
        contents:   { type: DataTypes.STRING(255), allowNull: false },
        keyword:    { type: DataTypes.STRING(255), allowNull: true },
        image_name: { type: DataTypes.STRING(255), allowNull: true },
        ar_width:   { type: DataTypes.DOUBLE, allowNull: true },
        ar_height:  { type: DataTypes.DOUBLE, allowNull: true },
        ar_depth:   { type: DataTypes.DOUBLE, allowNull: true },
    }, { tableName: 'post' });

    post.associate = (models) => {
        post.belongsTo(models.user);
        post.belongsTo(models.armodel);
    }
    
    return post;
};