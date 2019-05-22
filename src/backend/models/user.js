module.exports = (sequelize, DataTypes) => {
    let user = sequelize.define('user', {
        id:         { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
        username:   { type: DataTypes.STRING(30), unique: true, allowNull: false },
        password:   { type: DataTypes.STRING(255), allowNull: false },
        nickname:   { type: DataTypes.STRING(30), allowNull: false },
        address:    { type: DataTypes.STRING(255), allowNull: false },
        interest:   { type: DataTypes.STRING(255), allowNull: true },
        score:      { type: DataTypes.INTEGER, allowNull: false },
    }, { tableName: 'user' });
    user.associate = (models) => {
        user.hasMany(models.post, {onDelete: 'cascade', hooks: true});
    };
    return user;
};